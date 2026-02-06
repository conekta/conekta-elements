import {
  APPLE_PAY_MERCHANT_CAPABILITIES,
  APPLE_PAY_PAYMENT_DETAILS,
  APPLE_PAY_PAYMENTS_OPTIONS,
  APPLE_PAY_SUPPORTED_METHODS,
  APPLE_PAY_SUPPORTED_NETWORKS,
  APPLE_PAY_VERSION,
} from '../config';
import {
  WALLET_PAY_COUNTRY_CODE_DEFAULT,
  WALLET_PAY_NAME_DEFAULT,
} from '../config';

const applePayPaymentMethodData = (merchantId: string): PaymentMethodData[] => [
  {
    data: {
      countryCode: WALLET_PAY_COUNTRY_CODE_DEFAULT,
      merchantCapabilities: APPLE_PAY_MERCHANT_CAPABILITIES,
      merchantIdentifier: merchantId,
      supportedNetworks: APPLE_PAY_SUPPORTED_NETWORKS,
      version: APPLE_PAY_VERSION,
    },
    supportedMethods: APPLE_PAY_SUPPORTED_METHODS,
  },
];

export const createApplePayPaymentRequest = (
  amount: string,
  currencyCode: string,
  merchantId: string,
  requireCustomerInfo?: boolean,
): ApplePayPaymentRequest => ({
  countryCode: WALLET_PAY_COUNTRY_CODE_DEFAULT,
  currencyCode,
  merchantCapabilities: APPLE_PAY_MERCHANT_CAPABILITIES,
  merchantIdentifier: merchantId,
  requiredShippingContactFields: requireCustomerInfo ? ['name', 'phone', 'email'] : [],
  supportedNetworks: APPLE_PAY_SUPPORTED_NETWORKS,
  total: {
    amount,
    label: WALLET_PAY_NAME_DEFAULT,
    type: 'final',
  },
});

export const createMerchantPaymentRequest = (
  value: string,
  currency: string,
  merchantId: string,
  requireCustomerInfo?: boolean,
): PaymentRequest => {
  const paymentMethodData = applePayPaymentMethodData(merchantId);
  const paymentDetails = APPLE_PAY_PAYMENT_DETAILS(value, currency);
  const paymentOptions = requireCustomerInfo ? APPLE_PAY_PAYMENTS_OPTIONS : ({} as PaymentOptions);
  const paymentRequest = new window.PaymentRequest(paymentMethodData, paymentDetails, paymentOptions);
  return paymentRequest;
};
