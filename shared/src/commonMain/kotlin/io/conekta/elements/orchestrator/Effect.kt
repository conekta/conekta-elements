package io.conekta.elements.orchestrator

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import io.conekta.elements.orchestrator.PaymentMethod
import io.conekta.elements.orchestrator.ViewState

@OptIn(ExperimentalJsExport::class)
@JsExport
sealed class Effect {
    data class RpcSetActive(val method: PaymentMethod, val active: Boolean): Effect()
    data class RpcSetViewState(val method: PaymentMethod, val viewState: ViewState): Effect()
    data class RpcSubmit(val method: PaymentMethod): Effect()
    data class HostSetBlocked(val method: PaymentMethod, val blocked: Boolean): Effect()
    data class AnalyticsEvent(val name: String, val payloadJson: String) : Effect()
  }