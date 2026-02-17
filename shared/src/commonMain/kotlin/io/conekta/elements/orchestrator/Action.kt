package io.conekta.elements

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import io.conekta.elements.orchestrator.PaymentMethod
import io.conekta.elements.orchestrator.ViewState

@OptIn(ExperimentalJsExport::class)
@JsExport
sealed class Action {
    data class MethodMounted(val method: PaymentMethod): Action()
    data class MethodReady(val method: PaymentMethod): Action()
    data class SetActive(val method: PaymentMethod): Action()
    data class MethodSubmitStarted(val method: PaymentMethod): Action()
    data class MethodResult(val method: PaymentMethod, val status: String, val payload: Map<String, Any?>?): Action()
    data class SetViewState(val viewState: ViewState): Action()
  }