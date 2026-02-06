export type WalletPayValidationError =
    | { type: 'missing_customer_info' }
    | { type: 'missing_shipping_contact' };

export const walletPayValidationErrorMessageKey = (e: WalletPayValidationError): string => {
    switch (e.type) {
        case 'missing_customer_info':
            return 'walletPay.error.missingCustomerInfo';
        case 'missing_shipping_contact':
            return 'walletPay.error.missingShippingContact';
    }
};
