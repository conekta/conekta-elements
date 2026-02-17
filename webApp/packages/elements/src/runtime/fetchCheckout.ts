export type CheckoutData = {
    allowedPaymentMethods: string[];
    needsShippingContact: boolean;
    hasBuyerInfo: boolean;
};

export const fetchCheckout = async (_checkoutRequestId: string): Promise<CheckoutData> => {
    // TODO: reemplazar por llamada real
    return {
        allowedPaymentMethods: ['applePay', 'googlePay'],
        needsShippingContact: false,
        hasBuyerInfo: true,
    };
};
