import { PaymentMethodType, ViewState as KmpViewState, ResultStatus } from 'shared';
import type { PaymentMethod, ViewState } from './types';

export const toKmpMethod = (m: PaymentMethod) => {
    switch (m) {
        case PaymentMethodType.Apple.name: return PaymentMethodType.Apple;
        case PaymentMethodType.Google.name: return PaymentMethodType.Google;
        case PaymentMethodType.PayByBank.name: return PaymentMethodType.PayByBank;
        case PaymentMethodType.Card.name: return PaymentMethodType.Card;
        case PaymentMethodType.Cash.name: return PaymentMethodType.Cash;
        case PaymentMethodType.BankTransfer.name: return PaymentMethodType.BankTransfer;
        case PaymentMethodType.Bnpl.name: return PaymentMethodType.Bnpl;
    }
};

export const toPaymentMethod = (m: PaymentMethodType) => m.name

export const toViewState = (v: KmpViewState) => v.name


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