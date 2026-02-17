import { registerMethodFactory } from '../../orchestrator/registry';
import { createGooglePayFactory } from './factory';

export const registerGooglePay = () => registerMethodFactory('googlePay', createGooglePayFactory);
