import type { WalletPayValidationError } from '../../domain/errors/errors';

export const mapWalletPayValidationErrorToConektaError = (
    err: WalletPayValidationError,
): any => {
    const param = err.type === 'missing_shipping_contact' ? 'shippingContact' : 'customerInfo';

    return {
        object: 'error',
        type: 'validation_error',
        details: [
            {
                code: err.type,
                message: 'Faltan campos requeridos',
                param,
            },
        ],
    } as any; // TODO: Implement IConektaError
};
