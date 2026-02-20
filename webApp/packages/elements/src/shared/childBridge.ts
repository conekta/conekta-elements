import type { MethodLifecycleEvent } from '../orchestrator/types';
import type { ElementRPC, ElementMountOptions, ResultEvent, ActionRequiredEvent, StateChangeEvent } from './types';
import type { ReadyEvent } from './types';

type XProps = ElementMountOptions & {
    export?: (exportsImpl: ElementRPC) => void;
};

export const getXProps = (): XProps | undefined => (window as any).xprops;

export const createChildBridge = () => {
    const xprops = getXProps();
    return {
        export: (exportsImpl: ElementRPC) => {
            if (!xprops?.export) {
                console.warn('[Payment Method iframe] No xprops.export (not mounted by Zoid parent).');
                return;
            }
            xprops.export(exportsImpl);
        },
        emitReady: (data: ReadyEvent) => xprops?.onReady?.(data),
        emitLifecycleEvent: (event: MethodLifecycleEvent) => xprops?.onLifecycleEvent?.(event),
        emitResult: (result: ResultEvent) => xprops?.onResult?.(result),
        emitActionRequired: (actionRequired: ActionRequiredEvent) => xprops?.onActionRequired?.(actionRequired),
        emitLog: (log: any) => xprops?.onLog?.(log),
        emitStateChange: (stateChange: StateChangeEvent) => xprops?.onStateChange?.(stateChange),
    };
};