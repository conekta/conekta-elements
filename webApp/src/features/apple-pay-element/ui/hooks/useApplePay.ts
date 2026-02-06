import { useEffect, useState, useCallback } from 'react';
import { WALLET_PAY_CURRENCY_DEFAULT } from '../../infrastructure/config';
import { startApplePayPayment } from '../../application/use-cases/startApplePayPayment';
import { applePayBrowserDriver } from '../../infrastructure/drivers/applePayBrowserDriver';
import { applePaySessionGateway } from '../../infrastructure/gateways/applePaySessionGateway';
import { getApplePayValidationUrlFromEvent } from '../../infrastructure/applePayValidationUrl';
import { ApplePaySessionProps } from '../../application/views/ApplePayInitialData';
import { useApplePayScriptLoader } from '../../infrastructure/drivers/applePayScriptLoader';

type StartStrategy = 'applePaySession' | 'paymentRequest';

interface IUseApplePay {
  isApplePayReadyOnDevice: boolean;
  isApplePaySessionAvailable: boolean;
  isPaymentRequestAvailable: boolean;
  start: (strategy: StartStrategy) => Promise<void>;
}

export const useApplePay = (
  amount: string,
  currency: string = WALLET_PAY_CURRENCY_DEFAULT,
  requireCustomerInfo: boolean,
  enabled: boolean,
  // createOrder: (token: ApplePayPaymentToken, contact: ApplePayPaymentContact) => Promise<void>,
  onCompleteOrder: () => void,
  onAbortOrder: () => void,
  sessionProps: ApplePaySessionProps,
  merchantId: string,
): IUseApplePay => {
  const { status: scriptStatus, load } = useApplePayScriptLoader();

  const [isApplePayReadyOnDevice, setIsApplePayReadyOnDevice] = useState(false);
  const [isPaymentRequestAvailable, setIsPaymentRequestAvailable] = useState(false);
  const [isApplePaySessionAvailable, setIsApplePaySessionAvailable] = useState(false);

  const checkApplePayAccessForDevice = async () => {
    const isAppleSdkReady = scriptStatus === 'ready';
    if (!isAppleSdkReady) return;

    const paymentRequestExists = 'PaymentRequest' in window;
    const applePaySessionExists = 'ApplePaySession' in window;

    setIsApplePayReadyOnDevice(paymentRequestExists || applePaySessionExists);

    if (paymentRequestExists && window.PaymentRequest) {
      applePayBrowserDriver.canMakePaymentWithPaymentRequest({ amount, currency, merchantId, requireCustomerInfo }).then(setIsPaymentRequestAvailable);
    }

    if (applePaySessionExists && window.ApplePaySession) {
      applePayBrowserDriver.canMakePaymentWithApplePaySession(merchantId).then(setIsApplePaySessionAvailable);
    }
  };

  useEffect(() => {
    if (enabled) load();
  }, [enabled]);

  useEffect(() => {
    if (scriptStatus) checkApplePayAccessForDevice();
  }, [scriptStatus]);

  const start = useCallback(
    async (strategy: StartStrategy) => {
      await startApplePayPayment(
        {
          strategy,
          amount,
          currency,
          merchantId,
          requireCustomerInfo,
          sessionProps,
        },
        {
          driver: applePayBrowserDriver,
          applePaySessionPort: applePaySessionGateway,
          getValidationUrlFromEvent: getApplePayValidationUrlFromEvent,
          // createOrder,
          onCompleteOrder,
          onAbortOrder,
        },
      );
    },
    [amount, currency, merchantId, requireCustomerInfo, sessionProps, onCompleteOrder, onAbortOrder,
      //createOrder
    ],
  );

  return {
    isApplePayReadyOnDevice,
    isPaymentRequestAvailable,
    isApplePaySessionAvailable,
    start,
  };
};
