import { createGooglePayComponent } from './component';
import type { MethodFactory } from '../../orchestrator/registry';
import type { ElementMountOptions } from '../../shared/types';

export const createGooglePayFactory: MethodFactory = (ctx) => {
    const GooglePayComponent = createGooglePayComponent(ctx.baseUrl);

    return (props: ElementMountOptions) => GooglePayComponent(props);
};