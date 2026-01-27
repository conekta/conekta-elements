import type { ExpressCheckoutProps } from '../types';
import { CDN } from '../../../utils/cdn';
export const ExpressCheckout = ({ publicKey, amount, currency }: ExpressCheckoutProps) => {
  return (
    <div>
      <img
        src={CDN.Icons.APPLE}
        alt="Apple Pay"
      />
      <h2>Express Checkout</h2>
      <h2>api key {publicKey}</h2>
      <p>Amount: {amount} {currency}</p>
    </div>
  );
};
