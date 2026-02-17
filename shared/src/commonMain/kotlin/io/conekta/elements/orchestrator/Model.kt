@file:OptIn(kotlin.js.ExperimentalJsExport::class)

package io.conekta.elements.orchestrator

import kotlin.js.JsExport

@JsExport
enum class PaymentMethodType {
  applePay,
  googlePay,
  payByBank,
  card,
  cash,
  bankTransfer,
  bnpl,
}

@JsExport
enum class Policy {
  express,
  single,
}

@JsExport
enum class ViewState {
  editing,
  shipping,
  submitting,
  success,
  error,
  disabled,
}

@JsExport
data class MethodUiState(
  val mounted: Boolean = false,
  val ready: Boolean = false,
  val active: Boolean = false,
  val enabled: Boolean = true,
  val blocked: Boolean = false,
  val visible: Boolean = false,
)

@JsExport
data class OrchestratorState(
  val policy: Policy,
  val viewState: ViewState = ViewState.editing,
  val activeMethod: PaymentMethodType? = null,
  // Para JS interop es mejor mapa por enum que Record<string>
  val methods: Map<PaymentMethodType, MethodUiState>,
)

@JsExport
sealed class Action {
  data class MethodMounted(val method: PaymentMethodType) : Action()
  data class MethodReady(val method: PaymentMethodType) : Action()
  data class SetActive(val method: PaymentMethodType) : Action()
  data class MethodSubmitStarted(val method: PaymentMethodType) : Action()
  data class SetViewState(val viewState: ViewState) : Action()
  data class MethodResult(val method: PaymentMethodType, val status: ResultStatus) : Action()
}

@JsExport
enum class ResultStatus {
  succeeded,
  failed,
  requires_action,
  unknown,
}