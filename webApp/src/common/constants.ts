export enum CheckoutStatus {
    CANCELED = 'Canceled',
    CANCELLED = 'Cancelled',
    EXPIRED = 'Expired',
    FINALIZED = 'Finalized',
    PAID = 'Paid',
    PENDING = 'Pending_payment',
    ISSUED = 'Issued',
}

export enum ConektaSource {
    EMBEDDED = 'checkout-embedded',
    TOKENIZER = 'token-card-form',
    PAYMENT_LINK = 'payment-link',
    REDIRECTED = 'checkout-redirect',
    COMPONENT = 'component',
    COMPONENT_TOKENIZER = 'component-tokenizer',
}
