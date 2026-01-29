export interface ExpressCheckoutProps {
  publicKey: string;
  amount: number;
  currency: string;
  layout?: 'horizontal' | 'vertical';
  spacing?: number;
  onPaymentCompleted?: (result: PaymentResult) => void;
}

export interface PaymentResult {
  status: 'success' | 'error';
  data?: unknown;
}

export interface ApplePayTokenResult {
  token: string;
  paymentMethod: unknown;
  billingContact?: unknown;
  shippingContact?: unknown;
}

export interface ApplePayButtonProps {
  onPaymentAuthorized?: (result: ApplePayTokenResult) => void | Promise<void>;
  disabled?: boolean;
  loading?: boolean;
  variant?: 'black' | 'white' | 'white-with-line';
  appearance?: 'light' | 'dark' | 'auto';
  borderRadius?: string;
  height?: number;
  width?: number | string;
}
