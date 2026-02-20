@file:OptIn(kotlin.js.ExperimentalJsExport::class)

package io.conekta.elements.orchestrator

import io.conekta.elements.orchestrator.Effect
import io.conekta.elements.orchestrator.Action
import io.conekta.elements.orchestrator.OrchestratorCore
import io.conekta.elements.orchestrator.PaymentMethodType
import io.conekta.elements.orchestrator.ViewState
import io.conekta.elements.orchestrator.Policy
import io.conekta.elements.orchestrator.MethodUiState
import io.conekta.elements.orchestrator.OrchestratorState
import io.conekta.elements.orchestrator.DispatchResult
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
class OrchestratorCore(private val policy: Policy) {
    private var state: OrchestratorState = createInitialState(policy)
  
    fun getState(): OrchestratorState = state
  
    fun reset() { state = createInitialState(policy) }
  
    fun dispatch(action: Action): Array<Effect> {
      val res = io.conekta.elements.orchestrator.dispatch(state, action) // reducer puro
      state = res.state
      return res.effects
    }
  }