# Entrega — `@conekta/elements` (`webApp/packages/elements`)

Este paquete es la **librería Web “core”** (sin framework) que actúa como:

- **Orchestrator (HOST/PARENT)**: monta iframes por método (Zoid), centraliza estado y expone comandos vía RPC.
- **Runtime “Elements”**: API única `ConektaElements.init(...)` para inicializar una “molecule” (p. ej. `expressCheckout`) y montar automáticamente los métodos disponibles según el checkout.
- **Bridge del CHILD**: helpers para que el iframe (SSR del método) exporte RPC y emita eventos al host.

La **lógica compartida** (Kotlin Multiplatform) se importa desde el paquete JS `shared` (compilado desde `shared/`).

---

## Builds y artefactos

En `package.json`:

- **npm (ESM/CJS)**:
  - `dist/npm/*` (salidas de Vite)
  - `dist/entries/npm/*.d.ts` (tipos de TS)
- **CDN (IIFE)**:
  - `dist/cdn/orchestrator.iife.js`
  - Expone `window.ConektaElements`
  - Incluye banner: `var process = ...` para evitar `process is not defined` en browser.

Scripts:

- `npm run build` = types + `build:npm` + `build:cdn`

---

## API pública (lo importante)

### 1) Orchestrator (host) — montar métodos en iframes

El host monta métodos usando **factories registradas** por método:

- `registerMethodFactory(methodName, factory)`
- `createOrchestrator({ baseUrl, locale, theme, fingerprint })`
- `orchestrator.mount(method, container, opts)`
- `orchestrator.setActiveFor(method, boolean)`
- `orchestrator.submit()` / `submitFor(method)`

Notas:
- `baseUrl` se normaliza (sin `/` final) y se inyecta al factory para componer la URL del iframe.
- Los callbacks (`onReady`, `onStateChange`, `onResult`, etc.) se “cablean” a un event bus interno.

### 2) Runtime “Elements” — `ConektaElements.init(...)`

`ConektaElements.init`:

- Registra dinámicamente lo necesario para la molecule (tree‑shaking por método).
- Crea un `ConektaJsClient` (desde `shared`) para traer:
  - checkout
  - feature flags
- Monta el layout de la molecule (slots por método) y luego monta cada método si:
  - está permitido por el checkout
  - el `MethodModule` del método dice `isEligible(...)`

La salida de `init` expone `destroy()` para unmount.

### 3) Contrato Host ↔ Child (RPC + eventos)

El contrato vive en `src/shared/types.ts` y se implementa sobre Zoid:

- **Eventos (child → host)**:
  - `onReady`, `onStateChange`, `onActionRequired`, `onLifecycleEvent`, `onResult`, `onLog`
- **RPC (host → child)**:
  - `submit()`, `reset()`, `setViewState(viewState)`, `setActive(active)`, `destroy()`

La implementación base de Zoid está en `iframeDefinition`:
- define `props` y `exports`
- `exports` espera que el child haga `window.xprops.export({ ...rpc })`

En el child, el helper `createChildBridge()`:
- lee `window.xprops`
- expone `export(...)` para publicar RPC
- expone `emitReady(...)`, `emitResult(...)`, etc. para notificar al host

---

## Zoid (regla crítica)

**Zoid requiere que la definición del componente (`zoid.create`) se cargue en ambos lados (host y child) con el mismo `tag`.**

Cómo quedó modelado:

- **Host**: usa `createZoidComponent({ tag, url })` para crear el componente apuntando al iframe remoto.
- **Child**: cada método expone un “registrar” (ej. `registerApplePayChild`) que hace `zoid.create({ ...iframeDefinition, tag, url: window.location.href })`.

Esto asegura que:
- el host puede montar el iframe
- el child recibe `window.xprops` y puede exportar RPC

---

## Registro por método (tree‑shaking)

Hay dos registros separados:

- **Factory del host** (montaje Zoid):
  - `registerApplePay()` → `registerMethodFactory(PaymentMethodType.Apple.name, createApplePayFactory)`
  - el factory crea el componente con `baseUrl` y devuelve una función `(props) => instance`

- **Módulo runtime** (eligibilidad + props extra):
  - `registerApplePayRuntime()` → `registerMethodModule(applePayModule)`
  - `applePayModule.isEligible(...)` y `buildMountProps(...)` usan reglas/DTOs desde `shared`

La molecule (`expressCheckout`, `applePay`, etc.) decide qué métodos registrar vía `registerMolecule(...)`.

---

## CDN

El build CDN vive en `src/entries/cdn/orchestrator.ts` y solo hace:

- `window.ConektaElements = ConektaElements`

Eso permite consumir el orchestrator/runtime sin bundler (script tag) y luego usar:
- `window.ConektaElements.init({ ... })`

---

## Qué hice aquí (resumen técnico)

- Implementé un **orchestrator host** que:
  - monta métodos con factories registradas
  - estandariza callbacks y RPC
  - normaliza `baseUrl` y evita errores comunes (container no encontrado, RPC aún no exportado)
- Implementé el **runtime** que conecta:
  - checkout + feature flags (vía `ConektaJsClient` de `shared`)
  - eligibility/policies (vía `shared`)
  - montaje de métodos y coordinación de UI state (vía `OrchestratorCore` + `OrchestrationEngineJs`)
- Dejé listo el **build CDN** con el fix mínimo para `process` en browser.
