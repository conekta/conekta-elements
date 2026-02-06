export const APPLE_PAY_BUTTON_STYLES = `
    @supports (-webkit-appearance: -apple-pay-button) {
        .apple-pay-button {
            display: inline-block;
            appearance: -apple-pay-button;
            -webkit-appearance: -apple-pay-button;
        }
        .apple-pay-button-black {
            -apple-pay-button-style: black;
        }
        .apple-pay-button-white {
            -apple-pay-button-style: white;
        }
        .apple-pay-button-white-with-line {
            -apple-pay-button-style: white-outline;
        }
        .apple-pay-button-legacy {
            display: none !important;
        }
    }
    @supports not (-webkit-appearance: -apple-pay-button) {
        .apple-pay-button {
            display: none !important;
        }
        .apple-pay-button-legacy {
            display: flex;
        }
    }`;
