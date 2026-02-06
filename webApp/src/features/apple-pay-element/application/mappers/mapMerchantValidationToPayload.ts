import type { IMerchantApplePaySessionPayload } from 'common/interface';
import type { ApplePaySessionProps } from '../views/ApplePayInitialData';

export const mapMerchantValidationToPayload = (input: {
    validationURL: string;
    isTrusted: boolean;
    methodName: string;
    sessionProps: ApplePaySessionProps;
}): IMerchantApplePaySessionPayload => {
    return {
        isTrusted: input.isTrusted,
        methodName: input.methodName,
        validationURL: input.validationURL,
        domain: input.sessionProps.domain,
        source: input.sessionProps.source,
        companyId: input.sessionProps.companyId,
    };
};