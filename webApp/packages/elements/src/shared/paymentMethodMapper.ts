import { PaymentMethodType as KmpPaymentMethodType } from 'shared';
import type { PaymentMethod } from './types';

export const toKmpMethod = (m: PaymentMethod) => {
    switch (m) {
        case 'applePay': return KmpPaymentMethodType.applePay;
        case 'googlePay': return KmpPaymentMethodType.googlePay;
        case 'payByBank': return KmpPaymentMethodType.payByBank;
        case 'card': return KmpPaymentMethodType.card;
        case 'cash': return KmpPaymentMethodType.cash;
        case 'bankTransfer': return KmpPaymentMethodType.bankTransfer;
        case 'bnpl': return KmpPaymentMethodType.bnpl;
    }
};
