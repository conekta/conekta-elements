import type { InitArgs } from '../public/types';
import type { PaymentMethod, ResultEvent } from '../shared/types';
import { createOrchestrator } from '../orchestrator/createOrchestrator';
import { resolveContainer } from './resolveContainer';
import { getMoleculeMethods } from './molecules/registry';

import type { MethodLifecycleEvent } from '../orchestrator/types';

import { ConektaJsClient, Effect, OrchestrationEngineJs, OrchestratorCore, Policy, ResultStatus, PaymentMethodType } from 'shared';
import { toKmpResultStatus, toPaymentMethod, toViewState } from '../shared/paymentMethodMapper';
import { getMethodModule } from './methods/registry';

export const createElementsRuntime = () => {

    const orchestrator = createOrchestrator();
    let slotByMethod: Record<PaymentMethod, HTMLElement> = {} as Record<PaymentMethod, HTMLElement>;

    const core = new OrchestratorCore(Policy.express);


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

    const effectRunner = {
        run: async (eff: Effect) => {
            if (eff instanceof Effect.RpcSetActive) {
                return orchestrator.setActiveFor(toPaymentMethod(eff.method), eff.active);
            }
            if (eff instanceof Effect.RpcSetViewState) {
                return orchestrator.setViewStateFor(toPaymentMethod(eff.method), toViewState(eff.viewState));
            }
            if (eff instanceof Effect.RpcSubmit) {
                return orchestrator.submitFor(toPaymentMethod(eff.method));
            }
            if (eff instanceof Effect.HostSetBlocked) {
                setBlocked(toPaymentMethod(eff.method), eff.blocked);
                return;
            }
        },
    };

    const engine = new OrchestrationEngineJs(core, effectRunner);

    const init = async (args: InitArgs) => {
        const conektaJsClient = new ConektaJsClient(args.locale ?? 'es', args.baseUrl);
        const container = resolveContainer(args.container);

        args.onInit?.({ checkoutRequestId: args.checkoutRequestId });

        // 1) fetch checkout
        const checkout = await conektaJsClient.getCheckoutById(args.checkoutRequestId);
        const allowed = checkout.allowedPaymentMethods;

        // 2) resolve molecule methods (expressCheckout = applePay + payByBank + googlePay etc)
        const moleculeMethods = getMoleculeMethods(args.molecule).filter((m) => allowed.includes(m));

        // 3) mount molecule layout (subcontainers)
        slotByMethod = mountMoleculeLayout(container, moleculeMethods);

        // 4) mount each method
        for (const method of moleculeMethods) {
            const module = getMethodModule(method);

            if (!module) continue;

            const eligible = await module.isEligible({ method, deps: { client: conektaJsClient, checkoutRequest: checkout, locale: args.locale, theme: args.theme } });
            if (!eligible) continue;

            const extraProps = await module.buildMountProps({ method, deps: { client: conektaJsClient, checkoutRequest: checkout, locale: args.locale, theme: args.theme } });

            orchestrator.mount(method.name, slotByMethod[method.name], {
                checkoutRequestId: args.checkoutRequestId,
                needsShippingContact: checkout.needsShippingContact,
                hasBuyerInfo: checkout.orderTemplate.customerInfo?.name !== '',
                locale: args.locale,
                theme: args.theme,
                ...extraProps,
                onReady: async () => await engine.onMethodReady(method),
                onStateChange: (_data: any) => { },
                onActionRequired: (_evt: any) => { },
                onLifecycleEvent: async (evt: MethodLifecycleEvent) => {
                    if (evt.type === 'SUBMIT_STARTED') {
                        await engine.onSubmitStarted(method);
                    }
                },
                onResult: async (result: ResultEvent) => {
                    const status = toKmpResultStatus(result.status);

                    if (status === ResultStatus.succeeded) {
                        args.onSuccess?.({ checkoutRequestId: args.checkoutRequestId, paymentMethod: method.name, payload: result.payload });
                    } else {
                        args.onError?.({ checkoutRequestId: args.checkoutRequestId, paymentMethod: method.name, error: result?.error ?? result });
                    }
                    await engine.onResult(method, status);
                },
                onLog: () => { },
            });
            await engine.onMethodMounted(method);
        }

        if (moleculeMethods.length > 0) {
            await engine.setActive(moleculeMethods[0]);
        }

        return {
            destroy: async () => {
                await Promise.all(moleculeMethods.map((m) => orchestrator.unmount(m.name)));
            },
        };
    };

    return { init };
};

// layout helper (web-only)
function mountMoleculeLayout(
    container: HTMLElement,
    methods: PaymentMethodType[]
): Record<PaymentMethod, HTMLElement> {
    container.innerHTML = '';
    const slots = {} as Record<PaymentMethod, HTMLElement>;

    for (const method of methods) {
        const slot = document.createElement('div');
        slot.dataset['method'] = method.name;
        container.appendChild(slot);
        slots[method.name] = slot;
    }

    return slots;
}
