package io.conekta.elements.orchestrator

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import io.conekta.elements.orchestrator.PaymentMethodType
import io.conekta.elements.orchestrator.ViewState

@OptIn(ExperimentalJsExport::class)
@JsExport
sealed class Effect {
    data class RpcSetActive(val method: PaymentMethodType, val active: Boolean): Effect()
    data class RpcSetViewState(val method: PaymentMethodType, val viewState: ViewState): Effect()
    data class RpcSubmit(val method: PaymentMethodType): Effect()
    data class HostSetBlocked(val method: PaymentMethodType, val blocked: Boolean): Effect()
    data class AnalyticsEvent(val name: String, val payloadJson: String) : Effect()
  }