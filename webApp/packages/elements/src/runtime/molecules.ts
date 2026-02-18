import type { MoleculeId } from '../public/types';
import { PaymentMethodType } from 'shared';

export const getMoleculeMethods = (id: MoleculeId): PaymentMethodType[] => {
    switch (id) {
        case 'expressCheckout':
            return [PaymentMethodType.Apple, PaymentMethodType.Google
                //,'payByBank', 
            ];
        default:
            return [PaymentMethodType.Apple];
    }
};
