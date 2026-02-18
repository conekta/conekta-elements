import { PaymentMethodType } from 'shared';
import { registerMethodFactory } from '../../orchestrator/registry';
import { createApplePayFactory } from './factory';

export const registerApplePay = () => registerMethodFactory(PaymentMethodType.Apple.name, createApplePayFactory);
