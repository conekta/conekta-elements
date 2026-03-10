/// <reference path="../../zoid.d.ts" />
import zoid from 'zoid';
import { iframeDefinition } from '../../orchestrator/transport/zoid/iframeDefinition';
import { GOOGLE_PAY_TAG } from './constants';

export const registerGooglePayChild = () => {
    zoid.create({
        ...iframeDefinition,
        tag: GOOGLE_PAY_TAG,
        url: window.location.href,
    });
};
