import type { PaymentMethod, ViewState } from '../shared/types';

export type Action =
    | { type: 'METHOD_MOUNTED'; method: PaymentMethod }
    | { type: 'METHOD_READY'; method: PaymentMethod }
    | { type: 'SET_ACTIVE'; method: PaymentMethod }
    | { type: 'METHOD_SUBMIT_STARTED'; method: PaymentMethod }
    | { type: 'METHOD_RESULT'; method: PaymentMethod; result: any }
    | { type: 'SET_VIEW_STATE'; viewState: ViewState };
