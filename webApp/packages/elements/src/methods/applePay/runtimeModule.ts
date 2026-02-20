import {
    ApplePayEligibilityInput,
    CustomerInfoDto,
    MerchantEligibilityInput,
    ResolveAppleCompanyIdInput,
    isApplePayEligibleForCheckout,
    isValidMerchantForApplePay,
    requiresCustomerInfo,
    resolveAppleCompanyId,
    PaymentMethodType,
} from 'shared';

import type { MethodModule } from '../../runtime/methods/MethodModule';
import { APPLE_PAY_FOR_INTEGRATION_FLAG } from './constants';

export const applePayModule: MethodModule = {
    method: PaymentMethodType.Apple,

    isEligible: async ({ method, deps }) => {
        const { client, checkoutRequest } = deps;

        const withSubscription = (checkoutRequest.plans?.length ?? 0) > 0;
        const isIntegration = checkoutRequest.type === 'Integration';

        const flag = await client.getFeatureFlagByName(`component:${checkoutRequest.companyId}`, APPLE_PAY_FOR_INTEGRATION_FLAG);
        const validMerchant = isValidMerchantForApplePay(
            new MerchantEligibilityInput(
                // !isIntegration, // right now we only support apple pay element for integration, in the future we need to check if its hosted with shopify
                false, // remove, this its just for testing
                isIntegration,
                Boolean(flag?.value)
            )
        );

        return isApplePayEligibleForCheckout(
            new ApplePayEligibilityInput(
                checkoutRequest.allowedPaymentMethods,
                checkoutRequest.status,
                withSubscription,
                method,
                validMerchant
            )
        );
    },

    buildMountProps: async ({ deps }) => {
        const { checkoutRequest } = deps;

        const requiresInfo = requiresCustomerInfo(
            checkoutRequest.orderTemplate.customerInfo
                ? new CustomerInfoDto(
                    checkoutRequest.orderTemplate.customerInfo.corporate,
                    checkoutRequest.orderTemplate.customerInfo.customerFingerprint,
                    checkoutRequest.orderTemplate.customerInfo.customerId,
                    checkoutRequest.orderTemplate.customerInfo.email,
                    checkoutRequest.orderTemplate.customerInfo.name,
                    checkoutRequest.orderTemplate.customerInfo.phone
                )
                : undefined
        );

        const appleCompanyId = resolveAppleCompanyId(
            new ResolveAppleCompanyIdInput(checkoutRequest.type === 'Integration', checkoutRequest.companyId)
        );
        return {
            appleCompanyId,
            requiresInfo,
            checkoutRequest
        };
    },
};