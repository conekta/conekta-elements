import { selectApplePayStartStrategy } from './selectApplePayStartStrategy';
import type { ApplePayStartStrategy } from './selectApplePayStartStrategy';

type ExecutableStrategy = Exclude<ApplePayStartStrategy, 'none'>;

export const executeApplePayStart = async (input: {
    isApplePaySessionAvailable: boolean;
    isPaymentRequestAvailable: boolean;
    start: (strategy: ExecutableStrategy) => Promise<void>;
}) => {
    const strategy = selectApplePayStartStrategy(input);

    if (strategy === 'none') {
        // it should not pass if the button is enabled
        return;
    }

    await input.start(strategy);
};
