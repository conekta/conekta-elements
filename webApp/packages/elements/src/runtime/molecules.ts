import type { MoleculeId } from '../public/types';
import type { PaymentMethod } from '../shared/types';

export const getMoleculeMethods = (id: MoleculeId): PaymentMethod[] => {
    switch (id) {
        case 'expressCheckout':
            return ['applePay', 'googlePay'
                //,'payByBank', 
            ];
        default:
            return ['applePay'];
    }
};
