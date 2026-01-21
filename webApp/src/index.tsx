import React from 'react';
import ReactDOM from 'react-dom/client';
import { ConektaProvider, ExpressCheckout } from './index';

const rootElement = document.getElementById('root');
if (!rootElement) throw new Error('Failed to find the root element');

function App() {
  return (
    <div style={{ padding: '40px', maxWidth: '600px', margin: '0 auto' }}>
      <h1>Conekta Elements - Express Checkout Demo</h1>
      <p>Este es el demo básico del componente Express Checkout</p>
      
      <ConektaProvider publicKey="key_demo_123">
        <ExpressCheckout
          publicKey="key_demo_123"
          amount={10000}
          currency="MXN"
        />
      </ConektaProvider>
    </div>
  );
}

ReactDOM.createRoot(rootElement).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

