import { createZoidComponent } from '../../orchestrator/transport/zoid/createZoidComponent';
import { APPLE_PAY_TAG } from './constants';

export const createApplePayComponent = (baseUrl: string) => createZoidComponent({ tag: APPLE_PAY_TAG, url: `${baseUrl.replace(/\/$/, '')}/elements/apple-pay` });
