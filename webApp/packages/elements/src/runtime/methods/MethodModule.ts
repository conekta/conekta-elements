import type { PaymentMethodType, CheckoutDto, ConektaJsClient } from 'shared';

export type MethodRuntimeDeps = {
    client: ConektaJsClient;
    checkoutRequest: CheckoutDto;
    locale?: string;
    theme?: any;
};

export type MethodMountContext = {
    method: PaymentMethodType;
    deps: MethodRuntimeDeps;
};

export type MethodModule = {
    method: PaymentMethodType;
    isEligible: (ctx: MethodMountContext) => Promise<boolean>;
    buildMountProps: (ctx: MethodMountContext) => Promise<Record<string, any>>;
};