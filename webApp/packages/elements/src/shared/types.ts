import type { MethodLifecycleEvent } from "../orchestrator/types";

export type ReadyEvent = {
    paymentMethod: string;
    apiVersion: number;
    iframeVersion: string;
    capabilities: {
        supportsShippingGate?: boolean;
        supportsResume?: boolean;
        supportsPrefill?: boolean;
    };
};

export type StateChangeEvent = {
    paymentMethod: string;
    viewState: 'editing' | 'shipping' | 'submitting' | 'success' | 'error' | 'disabled';
    active: boolean;
    valid?: boolean;
    complete?: boolean;
    loading?: boolean;
    errorCode?: string;
};

export type ActionRequiredEvent = {
    paymentMethod: string;
    type: '3ds' | 'redirect' | 'popup' | 'deeplink';
    mode?: 'iframe' | 'popup' | 'redirect';
    payload: Record<string, any>;
};

export type ResultEvent =
    | { paymentMethod: string; status: 'succeeded'; payload: Record<string, any> }
    | { paymentMethod: string; status: 'failed'; error: { code: string; message?: string } };


export type PaymentMethod = 'applePay' | 'googlePay' | 'card' | 'cash' | 'bankTransfer' | 'bnpl' | 'payByBank';

export type ViewState = 'editing' | 'shipping' | 'submitting' | 'success' | 'error' | 'disabled';

export type OrchestratorConfig = {
    baseUrl?: string;
    locale?: string;
    theme?: Record<string, any>;
    sdkVersion?: string;
    correlationId?: string;
};

export type ElementMountOptions = {
    checkoutRequestId: string;
    needsShippingContact: boolean;
    hasBuyerInfo: boolean;
    checkoutRequestType?: 'HostedPayment' | 'Integration' | 'PaymentLink';
    viewState?: ViewState;
    active?: boolean;
    // callbacks - child -> parent
    onReady?: (e: any) => void;
    onStateChange?: (e: any) => void;
    onActionRequired?: (e: any) => void;
    onLifecycleEvent?: (e: MethodLifecycleEvent) => void;
    onResult?: (e: any) => void;
    onLog?: (e: any) => void;
};

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
