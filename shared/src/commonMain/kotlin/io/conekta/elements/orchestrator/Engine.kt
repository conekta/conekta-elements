package io.conekta.elements.orchestrator

import io.conekta.elements.orchestrator.Effect
import io.conekta.elements.orchestrator.Action
import io.conekta.elements.orchestrator.OrchestratorCore

interface EffectRunner {
    suspend fun run(effect: Effect)
  }
  
class OrchestrationEngine(
    private val core: OrchestratorCore,
    private val runner: EffectRunner
  ) {
    suspend fun dispatch(action: Action) {
        val effects: Array<Effect> = core.dispatch(action)
        for (eff in effects) {
          runner.run(eff)
        }
      }
  }
  