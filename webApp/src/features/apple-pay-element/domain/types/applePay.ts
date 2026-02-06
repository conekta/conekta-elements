import { CheckoutStatus } from 'common/constants';
import { PaymentMethodType } from 'common/util/constants';

export type ApplePayEligibilityInput = {
  allowedPaymentMethods: PaymentMethodType[];
  checkoutStatus: CheckoutStatus;
  withSubscription: boolean;
  paymentMethod: PaymentMethodType;
  isValidMerchantForApplePay: boolean;
};

export type MerchantEligibilityInput = {
  isHostedWithShopify: boolean;
  isIntegration: boolean;
  applePayForIntegrationEnabled: boolean;
};
