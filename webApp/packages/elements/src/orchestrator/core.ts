import { createInitialState } from './initialState';
import { reducer } from './reducer';
import type { Action } from './actions';
import type { Effect, MoleculePolicy, OrchestratorCoreState } from './state';

export const createOrchestratorCore = (policy: MoleculePolicy) => {
    let state: OrchestratorCoreState = createInitialState(policy);
    const listeners = new Set<(s: OrchestratorCoreState) => void>();

    const getState = () => state;

    const subscribe = (fn: (s: OrchestratorCoreState) => void) => {
        listeners.add(fn);
        return () => listeners.delete(fn);
    };

    const dispatch = (action: Action): Effect[] => {
        const { state: next, effects } = reducer(state, action);
        state = next;
        listeners.forEach((l) => l(state));
        return effects;
    };

    return { getState, subscribe, dispatch };
};

export type OrchestratorCore = ReturnType<typeof createOrchestratorCore>;