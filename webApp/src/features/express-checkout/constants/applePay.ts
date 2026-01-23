import { ApplePayMerchantCapability } from "../types";

export const APPLE_PAY_SDK_URL = 'https://applepay.cdn-apple.com/jsapi/1.latest/apple-pay-sdk.js';
export const APPLE_PAY_SUPPORTED_METHODS = 'https://apple.com/apple-pay';
export const APPLE_PAY_METHOD_NAME = 'https://apple.com/apple-pay';
export const APPLE_PAY_MERCHANT_CAPABILITIES: ApplePayMerchantCapability[] = ['supports3DS'];
export const APPLE_PAY_SUPPORTED_NETWORKS = ['amex', 'masterCard', 'visa'];
export const APPLE_PAY_VERSION = 3;
export const APPLE_PAY_COUNTRY_CODE_DEFAULT = 'MX';
export const APPLE_PAY_MERCHANT_NAME_DEFAULT = 'Conekta';

export const APPLE_PAY_BUTTON_STYLES = `
  @supports (-webkit-appearance: -apple-pay-button) {
    .apple-pay-button {
      display: inline-block;
      appearance: -apple-pay-button;
      -webkit-appearance: -apple-pay-button;
    }
    .apple-pay-button-black {
      -apple-pay-button-style: black;
    }
    .apple-pay-button-white {
      -apple-pay-button-style: white;
    }
    .apple-pay-button-white-with-line {
      -apple-pay-button-style: white-with-line;
    }
    .apple-pay-button-legacy {
      display: none !important;
    }
  }
  @supports not (-webkit-appearance: -apple-pay-button) {
    .apple-pay-button {
      display: none !important;
    }
    .apple-pay-button-legacy {
      display: flex;
    }
  }
`;
