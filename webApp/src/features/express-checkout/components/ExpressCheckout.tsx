import type { ExpressCheckoutProps } from '../types';

export const ExpressCheckout = ({ publicKey, amount, currency }: ExpressCheckoutProps) => {
  // TODO: Use publicKey for initializing payment gateway
  console.log('Initializing with key:', publicKey);
  
  return (
    <div>
      <h2>Express Checkout</h2>
      <p>Amount: {amount} {currency}</p>
    </div>
  );
};
