export const selectApplePayValidationUrl = (input: {
    isProduction: boolean;
    eventValidationUrl: string;
    fallbackValidationUrl: string;
}): string => {
    return input.isProduction ? input.eventValidationUrl : input.fallbackValidationUrl;
};
