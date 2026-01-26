import { ApplePayMerchantCapability } from "../types";

export const APPLE_PAY_SDK_URL = 'https://applepay.cdn-apple.com/jsapi/1.latest/apple-pay-sdk.js';
export const APPLE_PAY_SUPPORTED_METHODS = 'https://apple.com/apple-pay';
export const APPLE_PAY_METHOD_NAME = 'https://apple.com/apple-pay';
export const APPLE_PAY_MERCHANT_CAPABILITIES: ApplePayMerchantCapability[] = ['supports3DS'];
export const APPLE_PAY_SUPPORTED_NETWORKS = ['amex', 'masterCard', 'visa'];
export const APPLE_PAY_VERSION = 3;
export const APPLE_PAY_COUNTRY_CODE_DEFAULT = 'MX';
export const APPLE_PAY_MERCHANT_NAME_DEFAULT = 'Conekta';
