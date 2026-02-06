import { MerchantEligibilityInput } from '../types/applePay';

export const isValidMerchantForApplePay = ({
  isHostedWithShopify,
  isIntegration,
  applePayForIntegrationEnabled,
}: MerchantEligibilityInput): boolean => {
  const isIntegrationWithoutApplePay = isIntegration && !applePayForIntegrationEnabled;
  return !(isHostedWithShopify || isIntegrationWithoutApplePay);
};