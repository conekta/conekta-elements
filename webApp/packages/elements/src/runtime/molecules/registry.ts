import type { MoleculeId } from '../../public/types';
import { PaymentMethodType } from 'shared';

const MOLECULE_METHODS: Record<MoleculeId, PaymentMethodType[]> = {
    expressCheckout: [PaymentMethodType.Apple, PaymentMethodType.Google],
};

export const getMoleculeMethods = (id: MoleculeId): PaymentMethodType[] => {
    return MOLECULE_METHODS[id] ?? [];
};
