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

export interface ApplePayButtonProps {
  onClick?: () => void;
  disabled?: boolean;
  loading?: boolean;
  variant?: 'black' | 'white' | 'white-with-line';
  appearance?: 'light' | 'dark' | 'auto';
  borderRadius?: string;
  height?: number;
  width?: number | string;
}

export type ApplePayMerchantCapability = 'supports3DS' | 'supportsEMV' | 'supportsCredit' | 'supportsDebit';
