import React, { useState } from 'react';
import { ConektaProvider, ExpressCheckout } from '@conekta/elements';
import type { PaymentMethod, PaymentResult } from '@conekta/elements';

export function BasicExpressCheckout() {
  const handlePaymentMethodSelected = (method: PaymentMethod) => {
    console.log('Payment method selected:', method.type);
  };

  const handlePaymentCompleted = (result: PaymentResult) => {
    console.log('Payment completed:', result);
  };

  const handleError = (error: Error) => {
    console.error('Payment error:', error);
  };

  return (
    <ConektaProvider publicKey="key_your_public_key" environment="sandbox">
      <div className="checkout-container">
        <h2>Express Checkout</h2>
        
        <ExpressCheckout
          amount={10000}
          currency="MXN"
          onPaymentMethodSelected={handlePaymentMethodSelected}
          onPaymentCompleted={handlePaymentCompleted}
          onError={handleError}
        />
      </div>
    </ConektaProvider>
  );
}

export function CustomStyledExpressCheckout() {
  return (
    <ConektaProvider publicKey="key_your_public_key">
      <ExpressCheckout
        amount={25000}
        currency="MXN"
        appearance={{
          theme: 'dark',
          borderRadius: '12px',
          height: 60,
          width: 300,
        }}
        layout={{
          type: 'vertical',
          spacing: 16,
        }}
        onPaymentMethodSelected={(method) => console.log(method)}
        onPaymentCompleted={(result) => console.log(result)}
      />
    </ConektaProvider>
  );
}

export function StatefulExpressCheckout() {
  const [selectedMethod, setSelectedMethod] = useState<PaymentMethod | null>(null);
  const [isProcessing, setIsProcessing] = useState(false);
  const [paymentResult, setPaymentResult] = useState<PaymentResult | null>(null);
  const [error, setError] = useState<Error | null>(null);

  const handlePaymentMethodSelected = (method: PaymentMethod) => {
    setSelectedMethod(method);
    setIsProcessing(true);
    setError(null);
  };

  const handlePaymentCompleted = async (result: PaymentResult) => {
    setPaymentResult(result);
    setIsProcessing(false);

    try {
      const response = await fetch('/api/payments/verify', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          paymentId: result.id,
          orderId: 'ORDER_123',
        }),
      });

      if (!response.ok) {
        throw new Error('Payment verification failed');
      }

      window.location.href = '/payment-success';
    } catch (err) {
      setError(err as Error);
      setIsProcessing(false);
    }
  };

  const handleError = (err: Error) => {
    setError(err);
    setIsProcessing(false);
  };

  return (
    <ConektaProvider publicKey="key_your_public_key">
      <div className="checkout-page">
        <div className="order-summary">
          <h3>Order Summary</h3>
          <p>Total: $250.00 MXN</p>
        </div>

        <div className="payment-section">
          <h3>Pay with</h3>
          
          <ExpressCheckout
            amount={25000}
            currency="MXN"
            appearance={{ theme: 'auto' }}
            onPaymentMethodSelected={handlePaymentMethodSelected}
            onPaymentCompleted={handlePaymentCompleted}
            onError={handleError}
          />

          {isProcessing && (
            <div className="processing-indicator">
              <p>Processing payment...</p>
            </div>
          )}

          {error && (
            <div className="error-message">
              <p>Error: {error.message}</p>
              <button onClick={() => setError(null)}>Try Again</button>
            </div>
          )}

          {paymentResult && (
            <div className="success-message">
              <p>Payment successful! ID: {paymentResult.id}</p>
            </div>
          )}
        </div>
      </div>
    </ConektaProvider>
  );
}

export default BasicExpressCheckout;
