export { };

declare global {
  type PaymentCredentialStatus =
    | 'paymentCredentialsAvailable'
    | 'paymentCredentialStatusUnknown'
    | 'paymentCredentialsUnavailable'
    | 'applePayUnsupported';

  type ApplePayMerchantCapability = 'supports3DS' | 'supportsEMV' | 'supportsCredit' | 'supportsDebit';

  type ApplePayContactField = 'email' | 'name' | 'phone' | 'postalAddress' | 'phoneticName';

  type ApplePayLineItemType = 'final' | 'pending';

  type ApplePayPaymentMethodType = 'debit' | 'credit' | 'prepaid' | 'store';

  interface PaymentCredentialStatusResponse {
    paymentCredentialStatus: PaymentCredentialStatus;
  }

  interface ApplePayValidateMerchantEvent {
    validationURL: string;
  }

  interface ApplePayPaymentAuthorizedEvent {
    payment: ApplePayPayment;
  }

  interface ApplePayPaymentContact {
    phoneNumber: string;
    emailAddress: string;
    givenName: string;
    familyName: string;
    phoneticGivenName: string;
    phoneticFamilyName: string;
  }

  interface ApplePayPaymentMethod {
    displayName: string;
    network: string;
    type: ApplePayPaymentMethodType;
  }

  interface ApplePayPaymentToken {
    paymentMethod: ApplePayPaymentMethod;
    transactionIdentifier: string;
    paymentData: object;
  }

  interface ApplePayPayment {
    token: ApplePayPaymentToken;
    shippingContact: ApplePayPaymentContact;
  }

  interface ApplePayPaymentAuthorizationResult {
    status: number;
  }

  interface ApplePayLineItem {
    label: string;
    amount: string;
    type: ApplePayLineItemType;
  }

  interface ApplePayPaymentRequest {
    countryCode: string;
    currencyCode: string;
    merchantCapabilities: ApplePayMerchantCapability[];
    merchantIdentifier: string;
    requiredShippingContactFields: ApplePayContactField[];
    supportedNetworks: string[];
    total: ApplePayLineItem;
  }

  interface ApplePaySession {
    begin(): void;
    abort(): void;

    completePayment(result: ApplePayPaymentAuthorizationResult): void;
    completeMerchantValidation(merchantSession: unknown): void;

    onvalidatemerchant: ((event: ApplePayValidateMerchantEvent) => void) | null;
    onpaymentauthorized: ((event: ApplePayPaymentAuthorizedEvent) => void) | null;
    oncancel: (() => void) | null;
  }

  interface ApplePaySessionConstructor {
    new(version: number, paymentRequest: ApplePayPaymentRequest): ApplePaySession;

    STATUS_SUCCESS: number;
    STATUS_FAILURE: number;

    applePayCapabilities(merchantIdentifier: string): Promise<PaymentCredentialStatusResponse>;
  }

  interface MerchantValidationEvent extends Event {
    isTrusted: boolean;
    methodName: string;
    validationURL: string;
    complete: (merchantSession: Promise<any>) => void;
  }

  interface PaymentRequest {
    onmerchantvalidation: ((event: MerchantValidationEvent) => void) | null;
  }

  interface PaymentOptions {
    requestPayerEmail?: boolean;
    requestPayerName?: boolean;
    requestPayerPhone?: boolean;
  }

  interface Window {
    ApplePaySession: ApplePaySessionConstructor;
    PaymentRequest: typeof PaymentRequest;
  }
}
