import { ApplePaySessionProps } from "../views/ApplePayInitialData";

type MapApplePayOrderToPayloadInput = {
    PkPayment: ApplePayPaymentToken;
    contact: ApplePayPaymentContact;
    requireCustomerInfo: boolean;
    sessionProps: ApplePaySessionProps;
};

export const mapApplePayOrderToPayload = ({
    PkPayment,
    contact,
    requireCustomerInfo,
    sessionProps,
}: MapApplePayOrderToPayloadInput) => {
    const source = sessionProps?.source;

    return {
        PkPayment,
        ...(source && { source }),
        ...(requireCustomerInfo && {
            customerInfo: {
                email: contact?.emailAddress,
                name: [contact?.givenName, contact?.familyName].filter(Boolean).join(' '),
                phone: contact?.phoneNumber,
            },
        }),
    };
};
