import type { PaymentMethodType, ViewState as KmpViewState } from "shared";
import type { MethodLifecycleEvent } from "../orchestrator/types";

export type ReadyEvent = {
    paymentMethod: PaymentMethod;
    apiVersion: number;
    iframeVersion: string;
    capabilities: {
        supportsShippingGate?: boolean;
        supportsResume?: boolean;
        supportsPrefill?: boolean;
    };
};

export type StateChangeEvent = {
    paymentMethod: PaymentMethod;
    viewState: ViewState;
    active: boolean;
    valid?: boolean;
    complete?: boolean;
    loading?: boolean;
    errorCode?: string;
};

export type ActionRequiredEvent = {
    paymentMethod: PaymentMethod;
    type: '3ds' | 'redirect' | 'popup' | 'deeplink';
    mode?: 'iframe' | 'popup' | 'redirect';
    payload: Record<string, any>;
};

export type ResultEvent = { paymentMethod: PaymentMethod; status: 'succeeded' | 'requires_action' | 'failed'; payload?: Record<string, any>, error?: { code: string; message?: string } }


export type PaymentMethod = ReturnType<typeof PaymentMethodType.values>[number]['name'];

export type ViewState = ReturnType<typeof KmpViewState.values>[number]['name'];

export type OrchestratorConfig = {
    baseUrl?: string;
    locale?: string;
    theme?: Record<string, any>;
    fingerprint?: string;
};

export type ElementMountAttributes = {
    checkoutRequestId: string;
    needsShippingContact: boolean;
    hasBuyerInfo: boolean;
    checkoutRequestType?: 'HostedPayment' | 'Integration' | 'PaymentLink';
    viewState?: ViewState;
    active?: boolean;
    locale?: string;
    theme?: Record<string, any>;
    fingerprint?: string;
};

export type ElementMountCallbacks = {
    onReady?: (e: ReadyEvent) => void;
    onStateChange?: (e: StateChangeEvent) => void;
    onActionRequired?: (e: ActionRequiredEvent) => void;
    onLifecycleEvent?: (e: MethodLifecycleEvent) => void;
    onResult?: (e: ResultEvent) => void;
    onLog?: (e: any) => void;
};

export type ElementMountOptions = ElementMountAttributes & ElementMountCallbacks;

export type ElementRPC = {
    submit: () => Promise<void>;
    reset: () => Promise<void>;
    setViewState: (viewState: ViewState) => Promise<void>;
    setActive: (active: boolean) => Promise<void>;
    destroy: () => Promise<void>;
};

export type MountedElement = {
    method: PaymentMethod;
    container: HTMLElement;
    rpc: ElementRPC;
    destroy: () => Promise<void>;
};
