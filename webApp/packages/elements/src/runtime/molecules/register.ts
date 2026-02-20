import type { PaymentMethod } from '../../shared/types';
import { getMoleculeMethods } from './registry';

import { registerApplePay } from '../../methods/applePay/register';
import { registerGooglePay } from '../../methods/googlePay/register';

import { registerApplePayRuntime } from '../../methods/applePay/registerRuntime';
import { registerGooglePayRuntime } from '../../methods/googlePay/registerRuntime';

const REGISTRARS: Record<PaymentMethod, () => void> = {
    Apple: () => {
        registerApplePay();
        registerApplePayRuntime();
    },
    Google: () => {
        registerGooglePay();
        registerGooglePayRuntime();
    },
    Card: () => { },
    Cash: () => { },
    BankTransfer: () => { },
    Bnpl: () => { },
    PayByBank: () => { },
};

let registered = new Set<PaymentMethod>();

export const registerMolecule = (molecule: Parameters<typeof getMoleculeMethods>[0]) => {
    const methods = getMoleculeMethods(molecule);
    for (const m of methods) {
        if (registered.has(m.name)) continue;
        REGISTRARS[m.name]?.();
        registered.add(m.name);
    }
};