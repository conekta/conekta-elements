# Entrega — `webApp/packages`

Este folder contiene las **librerías Web** de Conekta Elements.

## Qué hay aquí

- **`elements/`** (`@conekta/elements`)
  - Librería **vanilla/agnóstica** (usable desde cualquier framework).
  - Incluye:
    - **Orchestrator (host/parent)**: monta iframes por método y expone RPC (`submit`, `setActive`, etc.).
    - **Runtime “Elements”**: API `ConektaElements.init(...)` para inicializar “molecules” (p. ej. `expressCheckout`) y montar múltiples métodos según el checkout.
    - **Build para npm** (ESM/CJS) + **build CDN** (IIFE que expone `window.ConektaElements`).
  - Enfoque clave: **tree‑shaking por método** vía subpaths/export explícitos.

- **`elements-react/`** (`@conekta/elements-react`)
  - Wrapper de React encima de `@conekta/elements`.
  - Provee componentes como `ExpressCheckout` y `ApplePayButton`, y un `ElementsProvider`.

## Dependencia “shared” (KMP → JS)

`@conekta/elements` depende de un módulo Kotlin Multiplatform compilado a JS con nombre de módulo **`shared`**:

- En `elements/package.json` se consume como dependencia local:
  - `shared: "file:../../shared/build/dist/js/developmentLibrary"`
- Ese artefacto lo genera Gradle desde el módulo `shared/` (fuera de este folder).

En otras palabras:
- **La UI/DOM y Zoid** viven en `packages/elements`.
- **La lógica compartida (estado, reglas, networking, DTOs)** vive en `shared/src` y se importa desde JS como `shared`.

## Cómo construir (resumen)

Cada paquete tiene scripts propios:

- **`packages/elements`**:
  - `npm run build` genera `dist/npm/*` y `dist/cdn/*`
- **`packages/elements-react`**:
  - `npm run build` genera `dist/*`

Requisitos: Node `>=18` (ver `engines` en cada `package.json`).

## Qué hice aquí (en alto nivel)

- Consolidé un **orchestrator host** que monta iframes por método y estandariza callbacks/eventos.
- Aseguré la separación:
  - **Host** (librería instalable/ CDN) vs **Child** (implementación SSR del método en iframe).
- Implementé el patrón de **registro por método** (factories + runtime modules) para habilitar tree‑shaking.
- Agregué build **CDN** con polyfill mínimo para `process` en browser (banner en Vite) para evitar `process is not defined`.

