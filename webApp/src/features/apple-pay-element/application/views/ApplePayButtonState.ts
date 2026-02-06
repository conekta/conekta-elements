import { PaymentMethodType } from "common/util/constants";
import { CheckoutStatus } from "common/constants";
import { CustomerInfo } from "common/interface";
import { ApplePaySessionProps } from "./ApplePayInitialData";

export type ApplePayButtonStateInput = {
    allowedPaymentMethods: PaymentMethodType[];
    checkoutStatus: CheckoutStatus;
    withSubscription: boolean;
    isIntegration: boolean;
    isHostedWithShopify: boolean;
    companyId: string;
    customerInfo: CustomerInfo | undefined;
    applePayForIntegrationEnabled: boolean;
};

export type ApplePayButtonState = {
    merchantId: string;
    sessionProps: ApplePaySessionProps;
    requireCustomerInfo: boolean;
    isEligibleForCheckout: boolean;
};