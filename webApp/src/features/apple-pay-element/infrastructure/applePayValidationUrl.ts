import { APPLE_PAY_VALIDATION_URL } from './config';
import { selectApplePayValidationUrl } from '../domain/policies/validationUrlPolicy';
import { isProductionEnv } from './env';

export const getApplePayValidationUrlFromEvent = (event: ApplePayValidateMerchantEvent): string => {
    return selectApplePayValidationUrl({
        isProduction: isProductionEnv(),
        eventValidationUrl: event.validationURL,
        fallbackValidationUrl: APPLE_PAY_VALIDATION_URL,
    });
};
