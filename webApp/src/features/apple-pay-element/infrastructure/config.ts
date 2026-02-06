import { CSSProperties } from "react";

export const APPLE_PAY_SDK_URL = 'https://applepay.cdn-apple.com/jsapi/1.latest/apple-pay-sdk.js';

export const APPLE_PAY_MERCHANT_IDENTIFIER = "merchant.io.conekta";
export const APPLE_PAY_SUPPORTED_METHODS = 'https://apple.com/apple-pay';
export const APPLE_PAY_MERCHANT_CAPABILITIES: ApplePayMerchantCapability[] = ['supports3DS'];
export const APPLE_PAY_SUPPORTED_NETWORKS = ['amex', 'masterCard', 'visa'];
export const APPLE_PAY_VERSION = 3;
export const APPLE_PAY_FOR_INTEGRATION_FLAG = 'ApplePayForIntegrationModule';

export const APPLE_PAY_PAYMENT_DETAILS = (value: string, currency: string): PaymentDetailsInit => ({
  total: {
    amount: {
      currency,
      value,
    },
    label: WALLET_PAY_NAME_DEFAULT,
  },
});

export const APPLE_PAY_PAYMENTS_OPTIONS: PaymentOptions = {
  requestPayerEmail: true,
  requestPayerName: true,
  requestPayerPhone: true,
};

export const APPLE_PAY_METHOD_NAME = 'https://apple.com/apple-pay';
export const APPLE_PAY_VALIDATION_URL = 'https://apple-pay-gateway-cert.apple.com/paymentservices/startSession';

export const WALLET_PAY_COUNTRY_CODE_DEFAULT = 'MX';
export const WALLET_PAY_CURRENCY_DEFAULT = 'MXN';
export const WALLET_PAY_NAME_DEFAULT = 'Conekta';
export const WALLET_PAY_IS_PRODUCTION = true;

export const APPLE_PAY_DEFAULTS = {
  countryCode: WALLET_PAY_COUNTRY_CODE_DEFAULT,
  label: WALLET_PAY_NAME_DEFAULT,
};

export const WALLET_PAY_BUTTON_STYLE_OPACITY_DEFAULT = 1;
export const WALLET_PAY_BUTTON_STYLE_OPACITY_DISABLED = 0.4;
export const WALLET_PAY_BUTTON_STYLE_HEIGHT_DEFAULT = '56px';
export const WALLET_PAY_BUTTON_STYLE_WIDTH_DEFAULT = '100%';

export const WALLET_PAY_BUTTON_STYLE_DEFAULT = (disabled: boolean): CSSProperties => ({
  height: WALLET_PAY_BUTTON_STYLE_HEIGHT_DEFAULT,
  opacity: disabled ? WALLET_PAY_BUTTON_STYLE_OPACITY_DISABLED : WALLET_PAY_BUTTON_STYLE_OPACITY_DEFAULT,
  width: WALLET_PAY_BUTTON_STYLE_WIDTH_DEFAULT,
});