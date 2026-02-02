import React from 'react';
import ReactDOM from 'react-dom/client';
import { MantineProvider } from '@mantine/core';
import { ConektaProvider, ExpressCheckout } from './';
import '@mantine/core/styles.css';

const rootElement = document.getElementById('root');
if (!rootElement) throw new Error('Failed to find the root element');

function App() {
  return (
    <MantineProvider>
      <div style={{ padding: '40px', maxWidth: '600px', margin: '0 auto' }}>
        <h1>Conekta Elements - Express Checkout Demo</h1>
        <p>Este es el demo básico del componente Express Checkout</p>
      <ConektaProvider publicKey="key_demo_123">
        <ExpressCheckout
          publicKey="key_demo_123"
          amount={10000}
          currency="MXN"
          layout="horizontal"   
          spacing={20}
        />
      </ConektaProvider>
      </div>
    </MantineProvider>
  );
}

ReactDOM.createRoot(rootElement).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

