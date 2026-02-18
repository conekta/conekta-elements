import { PaymentMethodType } from 'shared';
import { registerMethodFactory } from '../../orchestrator/registry';
import { createGooglePayFactory } from './factory';

export const registerGooglePay = () => registerMethodFactory(PaymentMethodType.Google.name, createGooglePayFactory);
