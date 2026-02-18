import { PaymentMethodType as KmpPaymentMethodType, ViewState as KmpViewState, ResultStatus } from 'shared';
import type { PaymentMethod, ViewState } from './types';

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

export const toPaymentMethod = (m: KmpPaymentMethodType) => {
    switch (m) {
        case KmpPaymentMethodType.applePay: return 'applePay';
        case KmpPaymentMethodType.googlePay: return 'googlePay';
        case KmpPaymentMethodType.payByBank: return 'payByBank';
        case KmpPaymentMethodType.card: return 'card';
        case KmpPaymentMethodType.cash: return 'cash';
        case KmpPaymentMethodType.bankTransfer: return 'bankTransfer';
        case KmpPaymentMethodType.bnpl: return 'bnpl';
        default: throw new Error(`Unknown payment method: ${m}`);
    }
};

export const toViewState = (v: KmpViewState) => {
    switch (v) {
        case KmpViewState.editing: return 'editing';
        case KmpViewState.shipping: return 'shipping';
        case KmpViewState.submitting: return 'submitting';
        case KmpViewState.success: return 'success';
        case KmpViewState.error: return 'error';
        case KmpViewState.disabled: return 'disabled';
        default: throw new Error(`Unknown view state: ${v}`);
    }
};

export const toKmpViewState = (v: ViewState) => {
    switch (v) {
        case 'editing': return KmpViewState.editing;
        case 'shipping': return KmpViewState.shipping;
        case 'submitting': return KmpViewState.submitting;
        case 'success': return KmpViewState.success;
        case 'error': return KmpViewState.error;
        case 'disabled': return KmpViewState.disabled;
        default: throw new Error(`Unknown view state: ${v}`);
    }
};

export const toResultStatus = (s: ResultStatus) => {
    switch (s) {
        case ResultStatus.succeeded: return 'succeeded';
        case ResultStatus.failed: return 'failed';
        case ResultStatus.requires_action: return 'requires_action';
        case ResultStatus.unknown: return 'unknown';
        default: throw new Error(`Unknown result status: ${s}`);
    }
};

export const toKmpResultStatus = (s: string) => {
    switch (s) {
        case 'succeeded': return ResultStatus.succeeded;
        case 'failed': return ResultStatus.failed;
        case 'requires_action': return ResultStatus.requires_action;
        default: throw new Error(`Unknown result status: ${s}`);
    }
};