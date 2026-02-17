import { ConektaElements } from '../../public';
import { registerApplePay } from '../../methods/applePay/register';
import { registerGooglePay } from '../../methods/googlePay/register';

declare global {
    interface Window {
        ConektaElements?: typeof ConektaElements;
    }
}

registerApplePay();
registerGooglePay();
window.ConektaElements = ConektaElements;
