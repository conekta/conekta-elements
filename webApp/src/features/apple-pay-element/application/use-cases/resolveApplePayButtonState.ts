import { PaymentMethodType } from "common/util/constants";
import { isApplePayEligibleForCheckout } from "../../domain/policies/applePayEligibilityPolicy";
import { APPLE_PAY_MERCHANT_IDENTIFIER } from "../../infrastructure/config";
import { resolveApplePayInitialData } from "./resolveApplePayInitialData";
import { getMerchantDomain } from "../../infrastructure/merchantDomain";
import { ApplePayButtonStateInput, ApplePayButtonState } from "../views/ApplePayButtonState";

export const resolveApplePayButtonState = (
    input: ApplePayButtonStateInput,
): ApplePayButtonState => {
    const domain = input.isIntegration ? getMerchantDomain() : undefined;

    const initialData = resolveApplePayInitialData({
        isIntegration: input.isIntegration,
        companyId: input.companyId,
        customerInfo: input.customerInfo,
        isHostedWithShopify: input.isHostedWithShopify,
        applePayForIntegrationEnabled: input.applePayForIntegrationEnabled,
        defaultMerchantId: APPLE_PAY_MERCHANT_IDENTIFIER,
        domain,
    });

    const isEligibleForCheckout = isApplePayEligibleForCheckout({
        allowedPaymentMethods: input.allowedPaymentMethods,
        checkoutStatus: input.checkoutStatus,
        withSubscription: input.withSubscription,
        paymentMethod: PaymentMethodType.Apple,
        isValidMerchantForApplePay: initialData.isValidMerchantForApplePay,
    });

    return {
        merchantId: initialData.merchantId,
        sessionProps: initialData.sessionProps,
        requireCustomerInfo: initialData.requireCustomerInfo,
        isEligibleForCheckout,
    };
};
