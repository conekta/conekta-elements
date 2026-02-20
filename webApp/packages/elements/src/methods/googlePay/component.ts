import { createZoidComponent } from '../../orchestrator/transport/zoid/createZoidComponent';
import { GOOGLE_PAY_TAG } from './constants';

export const createGooglePayComponent = (baseUrl: string) => createZoidComponent({ tag: GOOGLE_PAY_TAG, url: `${baseUrl.replace(/\/$/, '')}/elements/google-pay` });
