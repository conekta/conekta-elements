import { PaymentMethodType } from 'shared';
import type { MethodModule } from '../../runtime/methods/MethodModule';

export const googlePayModule: MethodModule = {
    method: PaymentMethodType.Google,
    isEligible: async () => true,
    buildMountProps: async () => ({}),
};