import { CheckoutStatus } from 'common/constants';
import type { ApplePayEligibilityInput } from '../types/applePay';

export const isApplePayEligibleForCheckout = ({
    allowedPaymentMethods,
    checkoutStatus,
    withSubscription,
    paymentMethod,
    isValidMerchantForApplePay,
}: ApplePayEligibilityInput): boolean => {
    const isAllowedForCheckout = allowedPaymentMethods.includes(paymentMethod);
    const isValidCheckoutStatus = checkoutStatus === CheckoutStatus.ISSUED;

    return isAllowedForCheckout && isValidCheckoutStatus && !withSubscription && isValidMerchantForApplePay;
};
