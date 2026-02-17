package io.conekta.elements

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import io.conekta.elements.orchestrator.PaymentMethodType
import io.conekta.elements.orchestrator.ViewState

@OptIn(ExperimentalJsExport::class)
@JsExport
sealed class Action {
    data class MethodMounted(val method: PaymentMethodType): Action()
    data class MethodReady(val method: PaymentMethodType): Action()
    data class SetActive(val method: PaymentMethodType): Action()
    data class MethodSubmitStarted(val method: PaymentMethodType): Action()
    data class MethodResult(val method: PaymentMethodType, val status: String, val payload: Map<String, Any?>?): Action()
    data class SetViewState(val viewState: ViewState): Action()
  }