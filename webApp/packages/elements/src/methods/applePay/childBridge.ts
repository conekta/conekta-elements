
import zoid from 'zoid';
import { iframeDefinition } from '../../orchestrator/transport/zoid/iframeDefinition';
import { APPLE_PAY_TAG } from './constants';

export const registerApplePayChild = () => {
    zoid.create({
        ...iframeDefinition,
        tag: APPLE_PAY_TAG,
        url: window.location.href,
    });
};
