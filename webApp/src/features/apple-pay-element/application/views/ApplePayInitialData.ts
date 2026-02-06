export type ApplePaySessionProps = {
  domain?: string;
  source?: string;
  companyId?: string;
};

export type ApplePayInitialData = {
  merchantId: string;
  sessionProps: ApplePaySessionProps;
  requireCustomerInfo: boolean;
  isValidMerchantForApplePay: boolean;
};