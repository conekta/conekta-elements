# Entrega — `shared/src` (Kotlin Multiplatform)

Este folder contiene el **código compartido** entre:

- **Mobile** (Android/iOS)
- **Web** (vía Kotlin/JS, importado en la app/librerías como módulo `shared`)

La idea es que aquí viva lo “core” y determinista:
- modelos/DTOs
- reglas/policies
- reducers + efectos declarativos (sin IO)
- networking multiplataforma (Ktor)
- utilidades que no dependan de UI/DOM

Y que fuera de aquí viva lo “platform-specific”:
- DOM/iframes/Zoid (web)
- pantallas nativas (mobile)
- envío real de analytics (host/web o mobile)

---

## Source sets

- **`commonMain/`**
  - Código 100% compartido: state machine del orchestrator, modelos, policies, tokenizer, networking base, recursos.

- **`jsMain/`**
  - Implementaciones/bridges específicos de Kotlin/JS:
    - wrappers que regresan `Promise` para consumo natural desde TypeScript
    - dependencias npm (ej. `crypto-js`, `jsencrypt`) para crypto/tokenizer
    - user agent/DateUtils específicos de JS

---

## Cómo se usa desde Web

El paquete `@conekta/elements` importa desde `shared`:

- `PaymentMethodType`, `Policy`, `ViewState`, `ResultStatus`
- `OrchestratorCore` (reducer puro + estado interno)
- `Effect` (efectos declarativos)
- `OrchestrationEngineJs` (engine para JS que ejecuta effects con un runner)
- `ConektaJsClient` (cliente de red que regresa `Promise`)
- policies como `isApplePayEligibleForCheckout`, `requiresCustomerInfo`, etc.

Y luego en Web:
- **KMP decide “qué hacer”** (effects)
- **el host web ejecuta “cómo hacerlo”** (RPC a iframes, overlays, analytics), implementando el runner en TypeScript

---

## Qué hice aquí (resumen)

- Definí/ajusté el **orchestrator core** como reducer puro con efectos declarativos (`Effect.*`).
- Exposé a JS lo necesario vía `@JsExport` y wrappers en `jsMain` para que el consumo en TS sea ergonómico (`Promise`, enums, DTOs).
- Agregué policies reutilizables (eligibilidad Apple Pay, validación merchant, customer info) que se consumen igual en Web y Mobile.
- Implementé el cliente `ConektaJsClient` para que Web pueda consumir endpoints de checkout/feature flags sin duplicar lógica de networking.

