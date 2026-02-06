export type ApplePayStartStrategy = 'applePaySession' | 'paymentRequest' | 'none';

export const selectApplePayStartStrategy = (input: {
    isApplePaySessionAvailable: boolean;
    isPaymentRequestAvailable: boolean;
}): ApplePayStartStrategy => {
    if (input.isApplePaySessionAvailable) return 'applePaySession';
    if (input.isPaymentRequestAvailable) return 'paymentRequest';
    return 'none';
};
