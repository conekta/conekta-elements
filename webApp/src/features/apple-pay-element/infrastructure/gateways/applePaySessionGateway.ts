import { getMerchantAppleSession } from '../applePayApi';
import { ApplePaySessionPort } from '../../application/ports/ApplePaySessionPort';


export const applePaySessionGateway: ApplePaySessionPort = {
    async createMerchantSession(payload) {
        const response = await getMerchantAppleSession(payload);
        return response;
    },
};
