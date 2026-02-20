import React from 'react';
import ReactDOM from 'react-dom/client';
import { MantineProvider } from '@mantine/core';
import { ExpressCheckout } from '../packages/elements-react/src/ExpressCheckout';
import '@mantine/core/styles.css';
import { ElementsProvider } from '../packages/elements-react/src/ElementsProvider';
// import { ApplePayButton } from '../packages/elements-react/src/ApplePayButton';

const rootElement = document.getElementById('root');
if (!rootElement) throw new Error('Failed to find the root element');

function App() {
  const checkoutRequestId = 'bced985b19be41dc8ef60dc5f670ceab';
  return (
    <MantineProvider>
      <div style={{ padding: '40px', maxWidth: '600px', margin: '0 auto' }}>
        <h1>Conekta Elements - Express Checkout Demo</h1>
        <p>Este es el demo básico del componente Express Checkout</p>
        <ElementsProvider>
          <ExpressCheckout
            checkoutRequestId={checkoutRequestId}
          />
          {/* <ApplePayButton
            checkoutRequestId={checkoutRequestId}
          /> */}
        </ElementsProvider>

      </div>
    </MantineProvider>
  );
}

ReactDOM.createRoot(rootElement).render(
  <App />
);

