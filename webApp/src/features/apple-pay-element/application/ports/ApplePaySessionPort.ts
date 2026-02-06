import type { IMerchantApplePaySessionPayload, IMerchantApplePaySessionResponse } from 'common/interface';

export type ApplePaySessionPort = {
    createMerchantSession(payload: IMerchantApplePaySessionPayload): Promise<IMerchantApplePaySessionResponse>;
};
