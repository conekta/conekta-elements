import type { ElementMountOptions, ElementRPC, PaymentMethod } from '../shared/types';

export type OrchestratorCtx = {
    baseUrl: string;
};

export type ZoidInstance = {
    render: (container: HTMLElement | string) => void;
    submit?: () => Promise<void>;
    reset?: () => Promise<void>;
    setViewState?: (viewState: any) => Promise<void>;
    setActive?: (active: boolean) => Promise<void>;
    destroy?: () => Promise<void>;
    close?: () => void;
};

export type ElementFactory = (props: ElementMountOptions) => ZoidInstance;
export type MethodFactory = (ctx: OrchestratorCtx) => ElementFactory;

const factories: Partial<Record<PaymentMethod, MethodFactory>> = {};

export const registerMethodFactory = (method: PaymentMethod, factory: MethodFactory) => {
    factories[method] = factory;
};

export const getMethodFactory = (method: PaymentMethod): MethodFactory => {
    const f = factories[method];
    if (!f) throw new Error(`No factory registered for method: ${method}`);
    return f;
};

export const toRPC = (instance: ZoidInstance): ElementRPC => ({
    submit: async () => {
        if (!instance.submit) throw new Error('RPC submit not ready (child did not export yet)');
        return instance.submit();
    },
    reset: async () => {
        if (!instance.reset) throw new Error('RPC reset not ready');
        return instance.reset();
    },
    setViewState: async (vs) => {
        if (!instance.setViewState) throw new Error('RPC setViewState not ready');
        return instance.setViewState(vs);
    },
    setActive: async (a) => {
        if (!instance.setActive) throw new Error('RPC setActive not ready');
        return instance.setActive(a);
    },
    destroy: async () => {
        if (!instance.destroy) return;
        return instance.destroy();
    },
});
