import type { ExpressCheckoutProps } from '../types';
import { CDN } from '../../../utils/cdn';
export const ExpressCheckout = ({ publicKey, amount, currency }: ExpressCheckoutProps) => {
  // TODO: Use publicKey for initializing payment gateway
  console.log('Initializing with key:', publicKey);
  return (
    <div>
    <img
            src={CDN.Icons.APPLE}
            alt="Apple Pay"
          />
      <h2>Express Checkout</h2>
      <p>Amount: {amount} {currency}</p>
    </div>
  );
};
