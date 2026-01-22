import type { ExpressCheckoutProps } from '../types';

export const ExpressCheckout = ({ publicKey, amount, currency }: ExpressCheckoutProps) => {
  return (
    <div>
      <h2>Express Checkout</h2>
      <p>Amount: {amount} {currency}</p>
    </div>
  );
};
