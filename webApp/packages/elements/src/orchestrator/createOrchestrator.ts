import type { ActionRequiredEvent, ElementMountOptions, MountedElement, OrchestratorConfig, PaymentMethod, ReadyEvent, ResultEvent, StateChangeEvent, ViewState } from '../shared/types';
import { createEventBus } from './events';
import { getMethodFactory, toRPC } from './registry';
import type { MethodLifecycleEvent, Orchestrator } from './types';

const resolveContainer = (target: string | HTMLElement): HTMLElement => {
    if (typeof target !== 'string') return target;
    const el = document.querySelector(target);
    if (!el) throw new Error(`Container not found: ${target}`);
    return el as HTMLElement;
};

const defaultBaseUrl = 'https://localhost:9092';

export const createOrchestrator = (config: OrchestratorConfig = {}): Orchestrator => {
    const baseUrl = (config.baseUrl ?? defaultBaseUrl).replace(/\/$/, '');
    const bus = createEventBus();

    const mounted = new Map<PaymentMethod, MountedElement>();
    let activeMethod: PaymentMethod | null = null;

    const mount = (method: PaymentMethod, target: string | HTMLElement, opts: ElementMountOptions) => {
        const container = resolveContainer(target);

        // 1) build factory
        const methodFactory = getMethodFactory(method);
        const elementFactory = methodFactory({ baseUrl });
        // 2) compose props
        const props: ElementMountOptions = {
            ...opts,
            locale: config.locale,
            theme: config.theme,
            fingerprint: config.fingerprint,

            // Wire callbacks -> event bus
            onReady: (data: ReadyEvent) => {
                opts.onReady?.(data);
                bus.emit('element:ready', { method, data });
            },
            onStateChange: (data: StateChangeEvent) => {
                opts.onStateChange?.(data);
                bus.emit('element:state', { method, data });
            },
            onActionRequired: (data: ActionRequiredEvent) => {
                opts.onActionRequired?.(data);
                bus.emit('element:action_required', { method, data });
            },
            onLifecycleEvent: (data: MethodLifecycleEvent) => {
                opts.onLifecycleEvent?.(data);
                bus.emit('element:lifecycle_event', { method, data });
            },
            onResult: (data: ResultEvent) => {
                opts.onResult?.(data);
                bus.emit('element:result', { method, data });
            },
            onLog: (data: any) => {
                opts.onLog?.(data);
            },
            onHeightListener: (height: number) => {
                container.style.height = `${height}px`;
            },
        };

        // 3) create instance & render
        const instance = elementFactory(props);

        // Varios SDKs aceptan string; preferible HTMLElement
        instance.render(container);

        // 4) rpc wrapper
        const rpc = toRPC(instance);

        const mountedEl: MountedElement = {
            method,
            container,
            rpc,
            destroy: async () => {
                try {
                    await rpc.destroy();
                } finally {
                    mounted.delete(method);
                    if (activeMethod === method) activeMethod = null;
                }
            },
        };

        mounted.set(method, mountedEl);
        return mountedEl;
    };

    const setActiveFor = async (method: PaymentMethod, active: boolean) => {
        const el = mounted.get(method);
        if (!el) throw new Error(`Method not mounted: ${method}`);
        await el.rpc.setActive(active);
    };

    const setViewState = async (viewState: ViewState) => {
        if (!activeMethod) throw new Error('No active method');
        const el = mounted.get(activeMethod);
        if (!el) throw new Error(`Active method not mounted: ${activeMethod}`);
        await el.rpc.setViewState(viewState);
    };

    const submit = async () => {
        if (!activeMethod) throw new Error('No active method');
        const el = mounted.get(activeMethod);
        if (!el) throw new Error(`Active method not mounted: ${activeMethod}`);
        await el.rpc.submit();
    };

    const unmount = async (method: PaymentMethod) => {
        const el = mounted.get(method);
        if (!el) return;
        await el.destroy();
    };

    const setViewStateFor = async (method: PaymentMethod, viewState: ViewState) => {
        const el = mounted.get(method);
        if (!el) throw new Error(`Method not mounted: ${method}`);
        await el.rpc.setViewState(viewState);
    };

    const submitFor = async (method: PaymentMethod) => {
        const el = mounted.get(method);
        if (!el) throw new Error(`Method not mounted: ${method}`);
        await el.rpc.submit();
    };

    return {
        mount,
        unmount,
        setActiveFor,
        setViewState,
        submit,
        setViewStateFor,
        submitFor,
        on: bus.on,
        off: bus.off,
    };
};
