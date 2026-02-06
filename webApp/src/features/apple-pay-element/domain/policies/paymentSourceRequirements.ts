
export type PaymentSourceRequirements = {
    requiresCustomerInfo: boolean;
    requiresShippingContact: boolean;
};

export const getPaymentSourceRequirements = (input: {
    needsShippingContact: boolean;
}): PaymentSourceRequirements => {
    const requiresShippingContact = input.needsShippingContact;
    const requiresCustomerInfo = true;

    return { requiresCustomerInfo, requiresShippingContact };
};
