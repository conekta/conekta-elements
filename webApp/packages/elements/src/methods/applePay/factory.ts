import { createApplePayComponent } from './component';
import type { MethodFactory } from '../../orchestrator/registry';
import type { ElementMountOptions } from '../../shared/types';

export const createApplePayFactory: MethodFactory = (ctx) => {
    const ApplePayComponent = createApplePayComponent(ctx.baseUrl);

    return (props: ElementMountOptions) => ApplePayComponent(props);
};