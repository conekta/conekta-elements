# Entrega (deep‑dive) — `shared/src/commonMain`

`commonMain` es el **núcleo multiplataforma**: aquí está lo que se comparte sin depender de UI/DOM, ni de iframes, ni de librerías específicas de plataforma.

La regla: **aquí se decide “qué hacer”**; las plataformas deciden “cómo hacerlo”.

---

## 1) Orchestrator core (reducer + efectos declarativos)

Ubicación: `kotlin/io/conekta/elements/orchestrator/*`

### Modelos y enums

En `Model.kt` están los tipos que consumen tanto Kotlin como JS (vía `@JsExport`):

- `PaymentMethodType` (Apple, Google, PayByBank, etc.)
- `Policy` (por ahora: `express` y `single`)
- `ViewState` (`editing`, `shipping`, `submitting`, `success`, `error`, `disabled`)
- `ResultStatus` (`succeeded`, `failed`, `requires_action`, `unknown`)
- `MethodUiState` (mounted/ready/active/enabled/blocked/visible)
- `OrchestratorState` (policy + viewState + activeMethod + map de states por método)

### Acciones

- `Action` es un `sealed class` con eventos de lifecycle:
  - `MethodMounted`, `MethodReady`
  - `SetActive`
  - `MethodSubmitStarted`
  - `SetViewState`
  - `MethodResult`

### Reducer puro + resultado

En `Reducer.kt`:

- `dispatch(state, action)` es **puro**: devuelve `DispatchResult(state, effects)`
- No hace IO. No conoce DOM. No llama red.
- Decide:
  - qué método está activo/visible
  - cuándo bloquear/desbloquear métodos
  - cómo evoluciona `viewState` con base en `ResultStatus`

### Efectos (lo que la plataforma debe ejecutar)

En `Effect.kt`:

- `RpcSetActive(method, active)`
- `RpcSetViewState(method, viewState)`
- `RpcSubmit(method)`
- `HostSetBlocked(method, blocked)` (side‑effect visual / UX, plataforma decide implementación)
- `AnalyticsEvent(name, payloadJson)` (plataforma decide envío real)

### Core con estado interno

En `Core.kt`:

- `OrchestratorCore` mantiene un `state` interno
- `dispatch(action)` aplica el reducer y regresa `effects`
- `reset()` restablece estado (vía `createInitialState` en `Dispatch.kt`)

### Engine multiplataforma

En `Engine.kt`:

- `OrchestrationEngine` recibe un `EffectRunner` (interface suspend)
- Ejecuta effects en orden

Este engine se usa directo en mobile; en web se usa el wrapper `OrchestrationEngineJs` (en `jsMain`) para integrarse con Promises.

---

## 2) Policies / Domain determinista (eligibilidad y reglas)

Ubicación: `kotlin/io/conekta/elements/policies/*` y `kotlin/io/conekta/elements/domain/*`

Aquí viven reglas puras reutilizables:

- `isApplePayEligibleForCheckout(ApplePayEligibilityInput)`
  - valida método permitido, status del checkout, que no haya suscripción, y merchant válido.
- `isValidMerchantForApplePay(MerchantEligibilityInput)`
  - bloquea casos como Shopify hosted o integración sin flag habilitado.
- `requiresCustomerInfo(CustomerInfo?)`
  - decide si falta info mínima (name/email) para continuar.
- `resolveAppleCompanyId(ResolveAppleCompanyIdInput)`
  - define qué merchant id usar según tipo de checkout (integration vs hosted).

Estas funciones son **shared**: el mismo “if” aplica en Web y Mobile.

---

## 3) Networking compartido (Ktor) + mapeos a DTO

Ubicación: `kotlin/io/conekta/elements/network/*` + `kotlin/io/conekta/elements/mappers/*` + `kotlin/io/conekta/elements/dtos/*`

Componentes:

- `ConektaHttpClient`
  - setea JSON (ignoreUnknownKeys/encodeDefaults) y logging (Kermit)
- `CheckoutSsrApiService`
  - `getCheckoutById`
  - `getFeatureFlagByName`
  - normaliza errores en `CheckoutSsrException`
- `CheckoutSsrConfig`
  - baseUrl/language/source (defaults)

El modelo “domain” (p. ej. `Checkout`, `FeatureFlag`) se mapea a DTOs (`CheckoutDto`, `FeatureFlagDto`, etc.) para exponer estructuras más estables y JS-friendly.

---

## Cómo se conecta con Web (resumen mental)

- Web (`@conekta/elements`) monta iframes y habla RPC.
- `commonMain` decide estado global → emite `Effect.*`.
- Web implementa el runner:
  - effects RPC → llamadas a Zoid (`submit`, `setActive`, `setViewState`)
  - `HostSetBlocked` → overlay/UX
  - `AnalyticsEvent` → sender real (pendiente/por implementar según plataforma)

