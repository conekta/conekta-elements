import {
    createMerchantPaymentRequest,
    createApplePayPaymentRequest,
} from '../factories/applePayRequestFactory';
import { APPLE_PAY_METHOD_NAME, APPLE_PAY_VERSION } from '../config';

export type ApplePayBrowserDriver = {
    createPaymentRequest(args: {
        amount: string;
        currency: string;
        merchantId: string;
        requireCustomerInfo: boolean;
    }): PaymentRequest;

    createApplePaySession(args: {
        amount: string;
        currency: string;
        merchantId: string;
        requireCustomerInfo: boolean;
    }): ApplePaySession;

    getMethodName(): string;

    getPaymentStatus(): { success: number; failure: number };

    completePayment(session: ApplePaySession, status: number): void;

    hasPaymentRequest(): boolean;
    hasApplePaySession(): boolean;

    canMakePaymentWithPaymentRequest(args: {
        amount: string;
        currency: string;
        merchantId: string;
        requireCustomerInfo: boolean;
    }): Promise<boolean>;

    canMakePaymentWithApplePaySession(merchantId: string): Promise<boolean>;
};

export const applePayBrowserDriver: ApplePayBrowserDriver = {
    createPaymentRequest: ({ amount, currency, merchantId, requireCustomerInfo }) =>
        createMerchantPaymentRequest(amount, currency, merchantId, requireCustomerInfo),

    createApplePaySession: ({ amount, currency, merchantId, requireCustomerInfo }) => {
        const Ctor = window.ApplePaySession;
        if (!Ctor) throw new Error('ApplePaySession constructor not available on window');
        const request = createApplePayPaymentRequest(amount, currency, merchantId, requireCustomerInfo);
        return new Ctor(APPLE_PAY_VERSION, request);
    },

    getMethodName: () => APPLE_PAY_METHOD_NAME,

    getPaymentStatus: () => {
        const Ctor = window.ApplePaySession;
        if (!Ctor) throw new Error('ApplePaySession not available');
        return { success: Ctor.STATUS_SUCCESS, failure: Ctor.STATUS_FAILURE };
    },

    completePayment: (session, status) => {
        session.completePayment({ status });
    },
    hasPaymentRequest: () => 'PaymentRequest' in window && typeof window.PaymentRequest === 'function',

    hasApplePaySession: () => 'ApplePaySession' in window && typeof window.ApplePaySession === 'function',

    canMakePaymentWithPaymentRequest: async ({ amount, currency, merchantId, requireCustomerInfo }) => {
        try {
            const paymentRequest = createMerchantPaymentRequest(amount, currency, merchantId, requireCustomerInfo);
            return await paymentRequest.canMakePayment();
        } catch (e) {
            console.log(e);
            return false;
        }
    },

    canMakePaymentWithApplePaySession: async (merchantId: string) => {
        try {
            const Ctor = window.ApplePaySession;
            if (!Ctor) return false;

            const capabilities = await Ctor.applePayCapabilities(merchantId);
            switch (capabilities.paymentCredentialStatus) {
                case 'paymentCredentialsAvailable':
                case 'paymentCredentialStatusUnknown':
                    return true;
                case 'paymentCredentialsUnavailable':
                case 'applePayUnsupported':
                default:
                    return false;
            }
        } catch (e) {
            console.log(e);
            return false;
        }
    },
};
