import type { ApplePaySessionProps } from '../views/ApplePayInitialData';
import type { ApplePayBrowserDriver } from '../../infrastructure/drivers/applePayBrowserDriver';

export type ApplePayStartStrategy = 'applePaySession' | 'paymentRequest';

import { mapMerchantValidationToPayload } from '../mappers/mapMerchantValidationToPayload';
import { ApplePaySessionPort } from '../ports/ApplePaySessionPort';

export type ApplePayStartDeps = {
    driver: ApplePayBrowserDriver;
    applePaySessionPort: ApplePaySessionPort;

    getValidationUrlFromEvent: (event: ApplePayValidateMerchantEvent) => string;
    // createOrder: (token: ApplePayPaymentToken, contact: ApplePayPaymentContact) => Promise<void>;
    onCompleteOrder: () => void;
    onAbortOrder: () => void;
};


export type StartApplePayPaymentInput = {
    strategy: ApplePayStartStrategy;
    amount: string;
    currency: string;
    merchantId: string;
    requireCustomerInfo: boolean;
    sessionProps: ApplePaySessionProps;
};

export const startApplePayPayment = async (
    input: StartApplePayPaymentInput,
    deps: ApplePayStartDeps,
): Promise<void> => {
    if (input.strategy === 'paymentRequest') {
        const request = deps.driver.createPaymentRequest({
            amount: input.amount,
            currency: input.currency,
            merchantId: input.merchantId,
            requireCustomerInfo: input.requireCustomerInfo,
        });

        let status: PaymentComplete = 'unknown';

        request.onmerchantvalidation = (event) => {
            const payload = mapMerchantValidationToPayload({
                validationURL: deps.getValidationUrlFromEvent(event as any),
                sessionProps: input.sessionProps,
                isTrusted: false,
                methodName: deps.driver.getMethodName(),
            });

            const merchantSessionPromise = deps.applePaySessionPort
                .createMerchantSession(payload)
                .catch((error) => {
                    deps.onAbortOrder();
                    request.abort();
                    console.log(error);
                    throw error;
                });

            event.complete(merchantSessionPromise);
        };


        try {
            const response = await request.show();

            try {
                // TODO: Implement create order
                // await deps.createOrder(response.details.token, response.details.shippingContact);
                console.log('apple pay response received', response);
                status = 'success';
            } catch (error) {
                status = 'fail';
                console.log(error);
            }

            await response.complete(status);
        } catch (error) {
            deps.onCompleteOrder();
            console.log(error);
        }

        return;
    }

    let session: ApplePaySession;

    try {
        session = deps.driver.createApplePaySession({
            amount: input.amount,
            currency: input.currency,
            merchantId: input.merchantId,
            requireCustomerInfo: input.requireCustomerInfo,
        });
    } catch (error) {
        deps.onAbortOrder();
        console.log(error);
        return;
    }

    session.onvalidatemerchant = async (event) => {
        const payload = mapMerchantValidationToPayload({
            validationURL: deps.getValidationUrlFromEvent(event),
            methodName: deps.driver.getMethodName(),
            isTrusted: false,
            sessionProps: input.sessionProps,
        });

        const merchantSession = await deps.applePaySessionPort
            .createMerchantSession(payload)
            .catch((error) => {
                deps.onAbortOrder();
                session.abort();
                console.log(error);
                throw error;
            });

        session.completeMerchantValidation(merchantSession);
    };


    session.onpaymentauthorized = async (event) => {
        let statuses: { success: number; failure: number };

        try {
            statuses = deps.driver.getPaymentStatus();
        } catch (error) {
            deps.onAbortOrder();
            console.log(error);
            session.abort();
            return;
        }

        let finalStatus = statuses.failure;

        try {
            // TODO: Implement create order
            // await deps.createOrder(event.payment.token, event.payment.shippingContact);
            console.log('apple pay payment authorized', event);
            finalStatus = statuses.success;
        } catch (error) {
            finalStatus = statuses.failure;
            console.log(error);
        }

        deps.driver.completePayment(session, finalStatus);
    };

    session.oncancel = () => {
        deps.onCompleteOrder();
    };

    session.begin();
};
