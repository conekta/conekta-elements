import type { InitArgs } from '../public/types';
import type { PaymentMethod } from '../shared/types';
import { createOrchestrator } from '../orchestrator/createOrchestrator';
import { resolveContainer } from './resolveContainer';
import { getMoleculeMethods } from './molecules';
import { fetchCheckout } from './fetchCheckout';

import { createOrchestratorCore } from '../orchestrator/core';
import type { MethodLifecycleEvent } from '../orchestrator/types';
import { createOrchestrationEngine, type EffectRunner } from '../orchestrator/engine';


export const createElementsRuntime = () => {
    const orchestrator = createOrchestrator();
    let slotByMethod: Record<PaymentMethod, HTMLElement> = {} as Record<PaymentMethod, HTMLElement>;

    const core = createOrchestratorCore('express');

    const setBlocked = (method: PaymentMethod, blocked: boolean) => {
        const slot = slotByMethod[method];
        if (!slot) return;
        slot.style.position = 'relative';

        let overlay = slot.querySelector(':scope > [data-overlay="blocked"]') as HTMLElement | null;

        if (blocked) {
            slot.style.pointerEvents = 'none';
            if (!overlay) {
                overlay = document.createElement('div');
                overlay.dataset.overlay = 'blocked';
                overlay.style.position = 'absolute';
                overlay.style.inset = '0';
                overlay.style.background = 'rgba(255,255,255,0.6)';
                overlay.style.borderRadius = '8px';
                slot.appendChild(overlay);
            }
        } else {
            slot.style.pointerEvents = '';
            overlay?.remove();
        }
    };

    const effectRunner: EffectRunner = {
        run: async (eff) => {
            switch (eff.type) {
                case 'RPC_SET_ACTIVE':
                    return orchestrator.setActiveFor(eff.method, eff.active);
                case 'RPC_SET_VIEW_STATE':
                    return orchestrator.setViewStateFor(eff.method, eff.viewState);
                case 'RPC_SUBMIT':
                    return orchestrator.submitFor(eff.method);
                case 'HOST_SET_BLOCKED':
                    return setBlocked(eff.method, eff.blocked);
            }
        },
    };

    const engine = createOrchestrationEngine(core, effectRunner);

    const init = async (args: InitArgs) => {
        const container = resolveContainer(args.container);

        args.onInit?.({ checkoutRequestId: args.checkoutRequestId });

        // 1) fetch checkout -> allowed methods (stub por ahora)
        const checkout = await fetchCheckout(args.checkoutRequestId);
        const allowed = checkout.allowedPaymentMethods as PaymentMethod[];

        // 2) resolve molecule methods (expressCheckout = applePay + payByBank + googlePay etc)
        const moleculeMethods = getMoleculeMethods(args.molecule).filter((m) => allowed.includes(m));

        // 3) mount molecule layout (subcontainers)
        slotByMethod = mountMoleculeLayout(container, moleculeMethods);

        // 4) mount each method
        moleculeMethods.forEach(async (method) => {
            orchestrator.mount(method, slotByMethod[method], {
                checkoutRequestId: args.checkoutRequestId,
                needsShippingContact: checkout.needsShippingContact,
                hasBuyerInfo: checkout.hasBuyerInfo,
                locale: args.locale,
                theme: args.theme,

                onReady: async () => await engine.onMethodReady(method),
                onStateChange: (_data: any) => { },
                onActionRequired: (_evt: any) => { },
                onLifecycleEvent: async (evt: MethodLifecycleEvent) => await engine.onLifecycleEvent(method, evt),
                onResult: async (result: any) => {
                    if (result?.status === 'succeeded') {
                        args.onSuccess?.({ checkoutRequestId: args.checkoutRequestId, paymentMethod: method, payload: result.payload });
                    } else {
                        args.onError?.({ checkoutRequestId: args.checkoutRequestId, paymentMethod: method, error: result?.error ?? result });
                    }
                    await engine.onResult(method, result);
                },
                onLog: () => { },
            } as any);
            await engine.onMethodMounted(method);
        });

        if (moleculeMethods.length > 0) {
            await engine.setActive(moleculeMethods[0]);
        }

        return {
            destroy: async () => {
                await Promise.all(moleculeMethods.map((m) => orchestrator.unmount(m)));
            },
        };
    };

    return { init };
};

// layout helper (web-only)
function mountMoleculeLayout(
    container: HTMLElement,
    methods: PaymentMethod[]
): Record<PaymentMethod, HTMLElement> {
    container.innerHTML = '';
    const slots = {} as Record<PaymentMethod, HTMLElement>;

    for (const method of methods) {
        const slot = document.createElement('div');
        slot.dataset['method'] = method;
        container.appendChild(slot);
        slots[method] = slot;
    }

    return slots;
}
