import type {
    ElementMountOptions,
    MountedElement,
    OrchestratorConfig,
    PaymentMethod,
    ViewState,
} from '../shared/types';
import type { OrchestratorEvents } from './events';

export type Unsubscribe = () => void;

export type Orchestrator = {
    mount: (
        method: PaymentMethod,
        target: string | HTMLElement,
        opts: ElementMountOptions
    ) => MountedElement;

    unmount: (method: PaymentMethod) => Promise<void>;

    setActiveFor: (method: PaymentMethod, active: boolean) => Promise<void>;

    setViewState: (viewState: ViewState) => Promise<void>;

    setViewStateFor: (method: PaymentMethod, viewState: ViewState) => Promise<void>

    submit: () => Promise<void>;

    submitFor: (method: PaymentMethod) => Promise<void>

    on: <K extends keyof OrchestratorEvents>(
        event: K,
        handler: (payload: OrchestratorEvents[K]) => void
    ) => Unsubscribe;

    off: <K extends keyof OrchestratorEvents>(
        event: K,
        handler: (payload: OrchestratorEvents[K]) => void
    ) => void;
};

export type CreateOrchestrator = (config?: OrchestratorConfig) => Orchestrator;

export type MethodLifecycleEvent =
    | { type: 'SUBMIT_STARTED' }
    | { type: 'SUBMIT_ENDED' }
    | { type: 'READY' };
