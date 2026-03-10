# Payments SDK – Arquitectura (Core + Shared Networking + UI SSR separada)

## 1. Scope real del proyecto (Core)

Construir un SDK de pagos multiplataforma que permita:

- Cumplimiento PCI, evitando que el merchant tenga acceso a datos sensibles

- Web: 
    - Uso desde React, Vue, Vanilla JS como lib instalable o Vanilla JS desde CDN.
    - Componente de pago completo o métodos de pago montables de forma independiente

- Mobile: SDK nativo para Android y iOS

Este repositorio contiene **exclusivamente**:

- **Core headless de Conekta Elements**
  - domain (state machines, reglas, validaciones, errores)
  - contratos (ports)
  - cliente de red compartido (service calls)
- **SDK Mobile** (Android / iOS) usando Kotlin Multiplatform

**No contiene UI web**  
**No contiene SSR ni rutas web**  

La UI web vive en **ct-checkout-fe** y consume este core como una **librería instalable**.

---

## 2. UI Web (repo separado – ct-checkout-fe)
ct-checkout-fe sera el encargado de lo relacionado a montar los iframes:

- Implementa **SSR**
- Define **rutas por element/método**, que serán las URLs cargadas en los iframes:
  - `/method/cash`
  - `/method/bank-transfer`
  - `/method/bnpl`
  - etc.
- Consume el **core** como dependencia (`@conekta/elements`)
- Implementa:
  - UI
  - formularios
  - pantallas de success/error
  - adapters web (fetch / lifecycle)

---

## 3. Arquitectura runtime (Web)

### Participantes
- **Merchant App** (React / Vue / Vanilla)
- **Orchestrator SDK (host runtime)**  
- **Element Iframes (SSR UI repo)**

### Principios
- Cada método de pago vive en **su propio iframe**
- Todos los iframes son **mismo dominio**
- Cada método de pago es autocontenido, pero no dueño del flujo global
- El orquestador:
  - monta iframes
  - Navegación entre vistas (form, shipping, success, error)
  - activa/desactiva métodos
  - controla el estado global
  - emite analytics
  - Comunicación host ↔ iframe (postMessage + RPC)
- Los métodos:
  - ejecutan su lógica
  - llaman al backend
  - devuelven resultados

3.1 Orquestador (Payments Core SDK – Web)

Implementado en TypeScript

Expuesto como:

    librería instalable
    bundle CDN

Wrapper inicial para React (a posteriori podemos implementar para Vue, Angular, etc).

3.2 Métodos de pago (Web)

Cada método = iframe propio, mismo dominio

Ejemplos:
    Apple
    Google
    Card
    Cash
    BankTransfer
    BNPL
    PayByBank

Responsables de:
    UI específica
    Validaciones locales
    Llamadas al backend
    Flujos externos (3DS, redirects, etc.)

Nunca:

Acceden a datos de otros métodos
Emiten analytics directamente al merchant

---

## 4. Modelo de control

### Regla clave
> El método **decide el resultado**  
> El orquestador **decide el estado global y la vista**

### Implicaciones
- El método **NO** navega por su cuenta a success/error
- El método emite eventos:
  - `RESULT { status, payload }`
- El orquestador:
  - cambia el estado global
  - decide mostrar success/error
  - Indica al método renderizar la UI final usando payload del método

### Beneficios
- UX consistente
- Analytics centralizados
- Compatible con lazy iframes + keep-alive
- Alineado con mobile
- Preparado para restore (F5) futuro

---

## 5. Comunicación Host ↔ Iframes

### Modelo
- **RPC** (postMessage) para acciones críticas (desde el orquestador a los métodos):
  - `submit()`
  - `resume()`
- **Eventos** para estado y telemetría (desde los métodos al orquestador):
  - `READY`
  - `STATE`
  - `ACTION_REQUIRED`
  - `RESULT`

**Flujo**

- El método ejecuta su lógica
- El método emite:
    RESULT { status, payload }

El orquestador:

- Cambia el estado global
- Decide mostrar success o error
- Indica al metodo que debe renderizar la UI final usando payload específico del método

### Regla
- Eventos = “algo pasó”
- RPC = “haz esto y dime el resultado”

---

## 6. Tree-shaking por método (requisito explícito)

### Objetivo
- El core debe ser **tree-shakeable por método**
- El orquestador también debe ser tree-shakeable
- Cada iframe importa **solo lo necesario de su element**
- El host no arrastra lógica de métodos no usados

### Diseño
- No usar “god modules” ni barrels globales
- Estructura por feature/método:
  - `core/cash/*`
  - `core/bank-transfer/*`
  - `core/bnpl/*`
- Entry points explícitos:
  - `@conekta/elements/cash`
  - `@conekta/elements/bank-transfer`
- Orquestador:
  - `@conekta/elements-orchestrator`

---

## 7. Separación clean-ish por capas

### Domain (compartido)
- modelos de dominio
- state machines
- validaciones
- reglas deterministas
- normalización de errores

### Application (compartido parcialmente – SOLO lógica pura)

En esta capa **NO se comparten use cases** ni flujos end-to-end.
Lo único que se comparte aquí es **lógica declarativa, pura y determinista**.

#### Lo que SÍ se comparte
- Reducers / state machines puras
  - calculan el siguiente estado a partir de:
    - estado actual
    - evento
    - contexto
  - no ejecutan IO
  - no conocen UI, red ni plataforma

- Definición declarativa de efectos
  - describen *qué* side-effects deben ocurrir
  - NO los ejecutan

Ejemplo conceptual de effect:
- `CREATE_PAYMENT_SOURCE`
- `CREATE_ORDER`

Estas definiciones:
- NO hacen fetch
- NO llaman servicios
- NO coordinan métodos
- NO deciden navegación

#### Lo que NO se comparte
- Use cases
- Orquestadores
- Flujos de UI
- Coordinación entre métodos
- Decisiones de UX


### Infrastructure (por plataforma)
- networking real
- lifecycle
- storage
- adapters

### Presentation (solo SSR / mobile UI)
- forms
- toasts
- pantallas success/error
- navegación

---

## 8. Qué se comparte entre Kotlin CMP y Web

### Shared (KMP `commonMain`)
- Domain:
  - estados y transiciones
  - reglas (gates como `needsShippingContact`)
  - validaciones buyer/shipping
  - errores normalizados
- Contratos:
  - modelos de input/output
  - success payloads
- **Service calls (networking)**

### NO compartido
- UI
- DOM / WebView
- postMessage / RPC runtime
- analytics sender
- Coordinación entre métodos (**Orquestador runtime, platform-specific**)
- **use cases orquestadores**

> **Nota sobre “coordinación entre métodos”**
>
> Esto se refiere explícitamente al **orquestador de pagos**, responsable de:
> - decidir qué método está activo
> - bloquear/desbloquear el checkout
> - controlar el estado global (editing / submitting / success / error)
> - manejar lazy iframes + keep-alive
> - emitir analytics
> - decidir qué vista se muestra (Modelo B)
>
> Aunque el concepto de orquestación es común, su **implementación NO se comparte**
> porque depende del runtime y la plataforma:
>
> - Web: iframes, postMessage, DOM, rutas SSR
> - Mobile: pantallas nativas, WebViews, deep links
>
> Cada plataforma implementa su propio orquestador.

---

## 9. Shared Networking (decisión final)

### Qué se comparte
- **Cliente de red KMP** (Ktor)
- Exportado a JS vía `@JsExport`
- Usado tanto en:
  - SDK Mobile
  - SSR web (iframes)

Ejemplo conceptual:
- `ConektaJsClient`
  - maneja auth, headers, idioma, versión API
  - encapsula endpoints (`getOrder`, `createOrder`, etc.)

### Qué NO se comparte
- Lógica de flujo (use cases)
- Decisiones de UI
- Analytics
- Coordinación entre métodos

### Motivo
- Networking es idéntico entre mobile y web
- Reduce duplicación
- Mantiene una única fuente de verdad de APIs
- El resto del flujo sigue siendo específico por plataforma

---

## 10. Use cases (decisión explícita)
- **NO se comparten**
- Viven en:
  - SSR repo (web)
  - SDK mobile
- Motivos:
  - fricción Kotlin/JS
  - bundle size
  - tree-shaking
  - ritmo distinto de evolución web/mobile
  - decisiones de UX

El core solo expone:
- domain
- reducers
- contratos
- networking

---

## 11. Cash + BankTransfer (primeros métodos)

### Comparten:
- misma state machine
- mismas validaciones
- mismo flujo base

### Diferencian:
- payload de success
- copy/UI

### Shipping gate (shared)
- Tras submit de buyer:
  - si `needsShippingContact = true` → mostrar form shipping
  - al submit shipping → continuar flujo
  - si `false` → continuar flujo directo

Esta lógica vive en domain/state machine compartida.

---

## 12. Analytics

### Regla
- **Un solo emisor**: el orquestador

### Shared incluye:
- nombres de eventos
- códigos de error
- helpers de payload

### Web/Mobile:
- implementan envío real (GTM, Firebase, etc.)

---

## 13. Regla de oro final
- Compartir **domain + contratos + networking**
- NO compartir UI ni use cases
- Entry points por método
- Orquestador manda
- Métodos ejecutan
