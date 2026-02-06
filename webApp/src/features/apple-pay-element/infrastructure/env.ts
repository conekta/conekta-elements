import { WALLET_PAY_IS_PRODUCTION } from './config';

export const isProductionEnv = (): boolean => WALLET_PAY_IS_PRODUCTION;
