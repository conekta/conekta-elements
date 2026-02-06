import { CheckoutRequest, Entity, Keys } from 'common/interface';
import { PaymentMethodType } from './constants';

export interface IImage {
  alt: string;
  className: string;
  src: string;
}

export interface CheckoutGetState {
  checkoutRequest: CheckoutRequest;
  entity: Entity;
  formattedAmount: number;
  amountInCurrencyFormat: string;
  isHigherThanCashLimitAmount: boolean;
  isOutBnplAmountRange: boolean;
  allowedPaymentMethods: PaymentMethodType[];
  isDatalogic: boolean;
  isPespay: boolean;
  isBbva: boolean;
}

export interface ViewState extends CheckoutGetState {
  keys: Keys;
  isIntegration: boolean;
  configuration?: Record<string, any>;
  orderTemplate?: {
    lineItems: Record<string, any>[];
  };
  device: Record<string, boolean>;
}

export interface PaymentMethodConfig {
  name: PaymentMethodType;
  isDisabled: boolean;
  icon: string;
}
