@file:OptIn(kotlin.js.ExperimentalJsExport::class)

package io.conekta.elements.orchestrator

import kotlin.js.JsExport
import io.conekta.elements.orchestrator.Action
import io.conekta.elements.orchestrator.Effect
import io.conekta.elements.orchestrator.OrchestratorState
import io.conekta.elements.orchestrator.PaymentMethodType
import io.conekta.elements.orchestrator.ViewState
import io.conekta.elements.orchestrator.Policy
import io.conekta.elements.orchestrator.MethodUiState

@JsExport
data class DispatchResult(
  val state: OrchestratorState,
  val effects: Array<Effect>,
)

@JsExport
fun dispatch(state: OrchestratorState, action: Action): DispatchResult {
  return when (action) {
    is Action.MethodMounted -> reduceMethodMounted(state, action.method)
    is Action.MethodReady -> reduceMethodReady(state, action.method)
    is Action.SetActive -> reduceSetActive(state, action.method)
    is Action.MethodSubmitStarted -> reduceSubmitStarted(state, action.method)
    is Action.SetViewState -> reduceSetViewState(state, action.viewState)
    is Action.MethodResult -> reduceMethodResult(state, action.method, action.status)
  }
}

private fun reduceMethodMounted(state: OrchestratorState, method: PaymentMethodType): DispatchResult {
  val current = state.methods[method] ?: MethodUiState()

  val visible = when (state.policy) {
    Policy.express -> true
    Policy.single -> state.activeMethod == null
  }

  val nextMethods = state.methods.toMutableMap()
  nextMethods[method] = current.copy(
    mounted = true,
    enabled = true,
    visible = visible
  )

  return DispatchResult(
    state = state.copy(methods = nextMethods),
    effects = emptyArray()
  )
}

private fun reduceMethodReady(state: OrchestratorState, method: PaymentMethodType): DispatchResult {
  val current = state.methods[method] ?: MethodUiState()
  val nextMethods = state.methods.toMutableMap()
  nextMethods[method] = current.copy(ready = true)

  return DispatchResult(
    state = state.copy(methods = nextMethods),
    effects = emptyArray()
  )
}

private fun reduceSetActive(state: OrchestratorState, method: PaymentMethodType): DispatchResult {
  val nextMethods = state.methods.toMutableMap()
  val effects = mutableListOf<Effect>()

  for ((m, current) in nextMethods) {
    val isActive = (m == method)

    val nextVisible = when (state.policy) {
      Policy.single -> isActive
      Policy.express -> current.visible
    }

    nextMethods[m] = current.copy(
      active = isActive,
      blocked = false,
      enabled = true,
      visible = nextVisible
    )

    if (current.mounted) {
      effects += Effect.RpcSetActive(m, isActive)
      effects += Effect.HostSetBlocked(m, false)
    }
  }

  effects += Effect.AnalyticsEvent(
    name = "method_selected",
    payloadJson = "{}" // TODO: add payload
  )

  return DispatchResult(
    state = state.copy(activeMethod = method, methods = nextMethods),
    effects = effects.toTypedArray()
  )
}

private fun reduceSubmitStarted(state: OrchestratorState, active: PaymentMethodType): DispatchResult {
  val nextMethods = state.methods.toMutableMap()
  val effects = mutableListOf<Effect>()

  for ((m, current) in nextMethods) {
    val isActive = (m == active)

    nextMethods[m] = current.copy(
      active = isActive,
      blocked = !isActive,
      enabled = isActive
    )

    if (current.mounted) {
      effects += Effect.HostSetBlocked(m, !isActive)
      effects += Effect.RpcSetActive(m, isActive)
    }
  }

  effects += Effect.RpcSetViewState(active, ViewState.submitting)
  effects += Effect.AnalyticsEvent(
    name = "submit_started",
    payloadJson = """{"method":"$active"}"""
  )

  return DispatchResult(
    state = state.copy(
      activeMethod = active,
      viewState = ViewState.submitting,
      methods = nextMethods
    ),
    effects = effects.toTypedArray()
  )
}

private fun reduceSetViewState(state: OrchestratorState, viewState: ViewState): DispatchResult {
  val effects = mutableListOf<Effect>()
  val active = state.activeMethod

  if (active != null) {
    effects += Effect.RpcSetViewState(active, viewState)
  }

  return DispatchResult(
    state = state.copy(viewState = viewState),
    effects = effects.toTypedArray()
  )
}

private fun reduceMethodResult(state: OrchestratorState, method: PaymentMethodType, status: ResultStatus): DispatchResult {
  val nextMethods = state.methods.toMutableMap()
  val effects = mutableListOf<Effect>()

  for ((m, current) in nextMethods) {
    nextMethods[m] = current.copy(blocked = false, enabled = true)
    if (current.mounted) effects += Effect.HostSetBlocked(m, false)
  }

  val nextViewState = when (status) {
    ResultStatus.succeeded -> ViewState.success
    ResultStatus.failed -> ViewState.error
    ResultStatus.requires_action -> ViewState.editing
    ResultStatus.unknown -> ViewState.editing
  }

  effects += Effect.AnalyticsEvent(
    name = "result",
    payloadJson = """{"method":"$method","status":"$status"}"""
  )

  return DispatchResult(
    state = state.copy(viewState = nextViewState, methods = nextMethods),
    effects = effects.toTypedArray()
  )
}
