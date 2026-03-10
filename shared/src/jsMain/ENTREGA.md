# Entrega (deep‑dive) — `shared/src/jsMain`

`jsMain` contiene lo **específico de Kotlin/JS**: implementaciones `actual`, wrappers a `Promise` y dependencias npm necesarias para que el core compartido pueda consumirse naturalmente desde TypeScript/Browser/Node.

---

## 1) Engine para JS: `OrchestrationEngineJs`

Ubicación: `kotlin/io/conekta/elements/orchestrator/Engine.kt`

Problema:
- En `commonMain`, `OrchestrationEngine` usa `suspend` + `EffectRunner`.
- En Web/TypeScript, lo más natural es consumir **Promesas**.

Solución:
- `EffectRunnerJs` es una `external interface` con:
  - `run(effect: Effect): Promise<Unit>`
- `OrchestrationEngineJs`:
  - llama `core.dispatch(action)` (sincrónico)
  - ejecuta los efectos en cadena con `.then(...)` para preservar el orden

Esto es lo que permite que el host web implemente el runner en TS y ejecute:
- RPC a iframes (Zoid)
- overlays de UI
- analytics

---

## 2) Cliente de red JS: `ConektaJsClient`

Ubicación: `kotlin/io/conekta/elements/network/ConektaJsClient.kt`

Qué hace:
- expone métodos JS-friendly (retornan `Promise`)
  - `getCheckoutById(id): Promise<CheckoutDto>`
  - `getFeatureFlagByName(appId, flagName): Promise<FeatureFlagDto>`
- internamente usa coroutines (`scope.promise { ... }`)
- se apoya en `CheckoutSsrApiService` (commonMain) y en mappers a DTO
- `close()` libera recursos del servicio/HttpClient

Esto se usa directamente en `@conekta/elements` para:
- traer checkout
- evaluar elegibilidad de métodos
- traer feature flags (p. ej. Apple Pay para Integration)

---

## Cómo se integra con el orchestrator web

En Web:
- El core (`OrchestratorCore` + reducer) vive en `commonMain`.
- `jsMain` habilita el puente:
  - effects → runner (Promise) → RPC/DOM
  - red → `ConektaJsClient` (Promise) → datos/flags para eligibility

El resultado es que el paquete web puede escribir lógica “orchestrator” en TS sin duplicar reglas ni modelos.

