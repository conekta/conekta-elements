@file:OptIn(kotlin.js.ExperimentalJsExport::class)

package io.conekta.elements.orchestrator

import kotlin.js.JsExport
import kotlin.js.Promise
import io.conekta.elements.orchestrator.Effect
import io.conekta.elements.orchestrator.Action
import io.conekta.elements.orchestrator.OrchestratorCore
import io.conekta.elements.orchestrator.OrchestratorState

@JsExport
external interface EffectRunnerJs {
  fun run(effect: Effect): Promise<Unit>
}

@JsExport
class OrchestrationEngineJs(
  private val core: OrchestratorCore,
  private val runner: EffectRunnerJs
) {
  fun dispatch(action: Action): Promise<Unit> {
    val effects = core.dispatch(action)

    var chain: Promise<Unit> = Promise.resolve(Unit)
    effects.forEach { eff ->
      chain = chain.then { runner.run(eff) }
    }
    return chain
  }

  fun onMethodMounted(method: PaymentMethod): Promise<Unit> =
    dispatch(Action.MethodMounted(method))

  fun onMethodReady(method: PaymentMethod): Promise<Unit> =
    dispatch(Action.MethodReady(method))

  fun setActive(method: PaymentMethod): Promise<Unit> =
    dispatch(Action.SetActive(method))

  fun onSubmitStarted(method: PaymentMethod): Promise<Unit> =
    dispatch(Action.MethodSubmitStarted(method))

  fun onResult(method: PaymentMethod, status: ResultStatus): Promise<Unit> =
    dispatch(Action.MethodResult(method, status))
}