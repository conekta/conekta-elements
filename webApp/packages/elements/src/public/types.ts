export type MoleculeId = 'expressCheckout' | 'applePay' | 'googlePay';

export type InitArgs = {
    checkoutRequestId: string;
    container: string | HTMLElement;
    molecule: MoleculeId;
    locale?: string;
    theme?: any;

    onInit?: (e: { checkoutRequestId: string }) => void;
    onSuccess?: (e: { checkoutRequestId: string; paymentMethod: string; payload: any }) => void;
    onError?: (e: { checkoutRequestId: string; paymentMethod?: string; error: any }) => void;
};
