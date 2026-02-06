import { requiresCustomerInfo } from '../../domain/policies/customerInfoPolicy';
import { isValidMerchantForApplePay } from '../../domain/policies/merchantPolicy';
import { CustomerInfo } from 'common/interface';
import { ApplePaySessionProps, ApplePayInitialData } from '../views/ApplePayInitialData';

type ResolveApplePayInitialDataInput = {
    isIntegration: boolean;
    companyId: string;
    customerInfo?: CustomerInfo;
    isHostedWithShopify: boolean;
    applePayForIntegrationEnabled: boolean;
    defaultMerchantId: string;

    domain?: string;
};



export const resolveApplePayInitialData = ({
    isIntegration,
    companyId,
    customerInfo,
    isHostedWithShopify,
    applePayForIntegrationEnabled,
    defaultMerchantId,
    domain,
}: ResolveApplePayInitialDataInput): ApplePayInitialData => {
    const requireCustomerInfo = requiresCustomerInfo(customerInfo);

    const isValidMerchant = isValidMerchantForApplePay({
        isHostedWithShopify,
        isIntegration,
        applePayForIntegrationEnabled,
    });

    const merchantId = isIntegration ? companyId : defaultMerchantId;

    const sessionProps: ApplePaySessionProps = isIntegration
        ? {
            ...(domain ? { domain } : {}),
            source: 'external',
            companyId,
        }
        : {};

    return {
        merchantId,
        sessionProps,
        requireCustomerInfo,
        isValidMerchantForApplePay: isValidMerchant,
    };
};
