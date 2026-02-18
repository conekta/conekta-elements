import type { Effect } from './state';
import type { MethodLifecycleEvent } from './types';
import type { PaymentMethod } from '../shared/types';
import type { OrchestratorCore } from './core';
import type { ResultEvent } from '../shared/types';

export type EffectRunner = {
    run: (effect: Effect) => Promise<void>;
};

export const createOrchestrationEngine = (
    core: OrchestratorCore,
    runner: EffectRunner
) => {
    const dispatchAndRun = async (action: any) => {
        const effects = core.dispatch(action);
        for (const eff of effects) {
            await runner.run(eff);
        }
    };

    return {
        onMethodMounted: (method: PaymentMethod) =>
            dispatchAndRun({ type: 'METHOD_MOUNTED', method }),

        onMethodReady: (method: PaymentMethod) =>
            dispatchAndRun({ type: 'METHOD_READY', method }),

        onLifecycleEvent: (method: PaymentMethod, evt: MethodLifecycleEvent) => {
            if (evt.type === 'SUBMIT_STARTED') {
                return dispatchAndRun({ type: 'METHOD_SUBMIT_STARTED', method });
            }
        },

        onResult: (method: PaymentMethod, result: ResultEvent) =>
            dispatchAndRun({ type: 'METHOD_RESULT', method, result }),

        setActive: (method: PaymentMethod) =>
            dispatchAndRun({ type: 'SET_ACTIVE', method }),
    };
};
