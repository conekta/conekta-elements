import { ApplePayMerchantCapability } from "../types";
import { CDNResources } from 'shared';

const resources = CDNResources.getInstance();

// Web-specific constant
export const APPLE_PAY_SDK_URL = 'https://applepay.cdn-apple.com/jsapi/1.latest/apple-pay-sdk.js';

// Re-export constants from shared module
export const APPLE_PAY_SUPPORTED_METHODS = resources.ApplePay.SUPPORTED_METHODS;
export const APPLE_PAY_METHOD_NAME = resources.ApplePay.METHOD_NAME;
export const APPLE_PAY_MERCHANT_CAPABILITIES: ApplePayMerchantCapability[] = resources.ApplePay.MERCHANT_CAPABILITIES as ApplePayMerchantCapability[];
export const APPLE_PAY_SUPPORTED_NETWORKS = resources.ApplePay.SUPPORTED_NETWORKS;
export const APPLE_PAY_VERSION = resources.ApplePay.VERSION;
export const APPLE_PAY_COUNTRY_CODE_DEFAULT = resources.ApplePay.COUNTRY_CODE_DEFAULT;
export const APPLE_PAY_MERCHANT_NAME_DEFAULT = resources.ApplePay.MERCHANT_NAME_DEFAULT;
