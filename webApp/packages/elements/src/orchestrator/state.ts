import type { PaymentMethod, ViewState } from '../shared/types';

export type MethodUIState = {
    mounted: boolean;
    ready: boolean;
    active: boolean;
    enabled: boolean;
    blocked: boolean;
    visible: boolean;
};

export type MoleculePolicy = 'express' | 'single';

export type OrchestratorCoreState = {
    viewState: ViewState;
    activeMethod?: PaymentMethod;
    policy: MoleculePolicy;
    methods: Record<PaymentMethod, MethodUIState>;
};

export type Effect =
    | { type: 'RPC_SET_ACTIVE'; method: PaymentMethod; active: boolean }
    | { type: 'RPC_SET_VIEW_STATE'; method: PaymentMethod; viewState: ViewState }
    | { type: 'RPC_SUBMIT'; method: PaymentMethod }
    | { type: 'HOST_SET_BLOCKED'; method: PaymentMethod; blocked: boolean };
