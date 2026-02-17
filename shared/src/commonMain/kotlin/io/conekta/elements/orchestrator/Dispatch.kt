@file:OptIn(kotlin.js.ExperimentalJsExport::class)

package io.conekta.elements.orchestrator

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import io.conekta.elements.orchestrator.PaymentMethodType
import io.conekta.elements.orchestrator.ViewState
import io.conekta.elements.orchestrator.Policy
import io.conekta.elements.orchestrator.OrchestratorState
import io.conekta.elements.orchestrator.MethodUiState

@JsExport
fun createInitialState(policy: Policy): OrchestratorState {
  val all = PaymentMethodType.entries.associateWith { MethodUiState() }
  return OrchestratorState(
    policy = policy,
    viewState = ViewState.editing,
    activeMethod = null,
    methods = all
  )
}
