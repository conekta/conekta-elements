import { TUseScriptStatus, useScript } from '../useScript';
import { APPLE_PAY_SDK_URL } from '../config';

export type ApplePayScriptLoader = {
    status: TUseScriptStatus;
    load(): void;
};

export const useApplePayScriptLoader = (): ApplePayScriptLoader => {
    const { status, loadScript } = useScript({
        removeOnUnmount: false,
        crossOrigin: 'anonymous',
    });

    const load = () => {
        loadScript(APPLE_PAY_SDK_URL);
    };

    return {
        status,
        load,
    };
};
