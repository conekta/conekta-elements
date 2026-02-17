import { registerMethodFactory } from '../../orchestrator/registry';
import { createApplePayFactory } from './factory';

export const registerApplePay = () => registerMethodFactory('applePay', createApplePayFactory);
