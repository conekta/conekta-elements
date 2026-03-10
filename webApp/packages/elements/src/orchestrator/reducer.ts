import type { PaymentMethod } from '../shared/types';
import type { Action } from './actions';
import type { Effect, OrchestratorCoreState } from './state';

export const reducer = (
    state: OrchestratorCoreState,
    action: Action
): { state: OrchestratorCoreState; effects: Effect[] } => {
    switch (action.type) {

        case 'METHOD_MOUNTED': {
            const current = state.methods[action.method];

            const nextMethod = {
                ...current,
                mounted: true,
                enabled: true,
                visible:
                    state.policy === 'express'
                        ? true
                        : !state.activeMethod, // single: solo el primero
            };

            return {
                state: {
                    ...state,
                    methods: {
                        ...state.methods,
                        [action.method]: nextMethod,
                    },
                },
                effects: [],
            };
        }

        case 'METHOD_READY': {
            return {
                state: {
                    ...state,
                    methods: {
                        ...state.methods,
                        [action.method]: {
                            ...state.methods[action.method],
                            ready: true,
                        },
                    },
                },
                effects: [],
            };
        }

        case 'SET_ACTIVE': {
            const effects: Effect[] = [];
            const nextMethods = { ...state.methods };

            for (const m of Object.keys(nextMethods) as PaymentMethod[]) {
                const isActive = m === action.method;
                const current = nextMethods[m];

                nextMethods[m] = {
                    ...current,
                    active: isActive,
                    blocked: false,
                    enabled: true,
                    visible:
                        state.policy === 'single'
                            ? isActive
                            : current.visible,
                };

                if (current.mounted) {
                    effects.push({ type: 'RPC_SET_ACTIVE', method: m, active: isActive });
                    effects.push({ type: 'HOST_SET_BLOCKED', method: m, blocked: false });
                }
            }

            return {
                state: {
                    ...state,
                    activeMethod: action.method,
                    methods: nextMethods,
                },
                effects,
            };
        }

        case 'METHOD_SUBMIT_STARTED': {
            const active = action.method;
            const effects: Effect[] = [];
            const nextMethods = { ...state.methods };

            for (const m of Object.keys(nextMethods) as PaymentMethod[]) {
                const isActive = m === active;
                const current = nextMethods[m];

                nextMethods[m] = {
                    ...current,
                    active: isActive,
                    blocked: !isActive,
                    enabled: isActive,
                };

                if (current.mounted) {
                    effects.push({ type: 'HOST_SET_BLOCKED', method: m, blocked: !isActive });
                    effects.push({ type: 'RPC_SET_ACTIVE', method: m, active: isActive });
                }
            }

            effects.push({
                type: 'RPC_SET_VIEW_STATE',
                method: active,
                viewState: 'submitting',
            });

            return {
                state: {
                    ...state,
                    activeMethod: active,
                    viewState: 'submitting',
                    methods: nextMethods,
                },
                effects,
            };
        }

        case 'SET_VIEW_STATE': {
            const effects: Effect[] = [];

            if (state.activeMethod) {
                effects.push({
                    type: 'RPC_SET_VIEW_STATE',
                    method: state.activeMethod,
                    viewState: action.viewState,
                });
            }

            return {
                state: { ...state, viewState: action.viewState },
                effects,
            };
        }

        case 'METHOD_RESULT': {
            const effects: Effect[] = [];
            const nextMethods = { ...state.methods };

            for (const m of Object.keys(nextMethods) as PaymentMethod[]) {
                const current = nextMethods[m];

                nextMethods[m] = {
                    ...current,
                    blocked: false,
                    enabled: true,
                };

                if (current.mounted) {
                    effects.push({ type: 'HOST_SET_BLOCKED', method: m, blocked: false });
                }
            }

            const status = action.result?.status;
            const viewState =
                status === 'succeeded'
                    ? 'success'
                    : status === 'failed'
                        ? 'error'
                        : 'editing';

            return {
                state: {
                    ...state,
                    viewState,
                    methods: nextMethods,
                },
                effects,
            };
        }

        default:
            return { state, effects: [] };
    }
};
