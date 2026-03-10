import type { PaymentMethodType } from 'shared';
import type { MethodModule } from './MethodModule';

const modules = new Map<string, MethodModule>();

export const registerMethodModule = (m: MethodModule) => {
    modules.set(m.method.name, m);
};

export const getMethodModule = (method: PaymentMethodType): MethodModule | undefined => {
    return modules.get(method.name);
};