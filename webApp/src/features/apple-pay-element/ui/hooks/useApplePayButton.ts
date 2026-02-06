import { useEffect, useState } from 'react';
import { useCheckoutFrameContext } from '../../application/stores/CheckoutFrameContext';
import { usePaymentFormStore } from '../../application/stores/usePaymentFormStore';
import { useApplePay } from './useApplePay';
import { isSubscription, selectIsHostedPayment, selectIsShopify, selecIsIntegration } from '../../application/stores/selectors';
import { WALLET_PAY_CURRENCY_DEFAULT } from '../../infrastructure/config';

import { featureFlagGateway } from '../../infrastructure/gateways/featureFlagGateway';
import { APPLE_PAY_FOR_INTEGRATION_FLAG } from '../../infrastructure/config';


import { executeApplePayStart } from '../../application/use-cases/executeApplePayStart';
import { resolveApplePayButtonState } from '../../application/use-cases/resolveApplePayButtonState';

export const useApplePayButton = () => {
  const { allowedPaymentMethods, formattedAmount, checkoutRequest } = useCheckoutFrameContext();
  const { isLoading: isApplePayLoading,
    setAppError,
  } = usePaymentFormStore();
  // const { handleWalletPayCreateOrder: handleApplePayCreateOrder } = useWalletPayOrder(PaymentMethodType.Apple);

  const [applePayForIntegrationEnabled, setApplePayForIntegrationEnabled] = useState(false);

  const isHostedWithShopify = selectIsHostedPayment() && selectIsShopify();
  const isIntegration = selecIsIntegration();
  const withSubscription = isSubscription()?.length > 0;

  const {
    orderTemplate: { currency = WALLET_PAY_CURRENCY_DEFAULT, customerInfo },
    status: checkoutStatus,
    companyId,
  } = checkoutRequest;

  const internalError = {
    response: { data: { checkoutMessage: 'hostedCheckout.internalError.title' } },
  } as unknown as any; // TODO: Implement IConektaError

  const {
    merchantId,
    sessionProps,
    requireCustomerInfo,
    isEligibleForCheckout,
  } = resolveApplePayButtonState({
    allowedPaymentMethods,
    checkoutStatus,
    withSubscription,
    isIntegration,
    isHostedWithShopify,
    companyId,
    customerInfo,
    applePayForIntegrationEnabled,
  });


  // const createApplePayOrder = async (PkPayment: ApplePayPaymentToken, contact: ApplePayPaymentContact) => {
  //   const payload = mapApplePayOrderToPayload({
  //     PkPayment,
  //     contact,
  //     requireCustomerInfo,
  //     sessionProps,
  //   });

  //   await handleApplePayCreateOrder(payload);
  // };

  const onCompleteApplePayOrder = () => {
    // TODO: EMIT EVENT TO PARENT COMPONENT
    console.log('apple pay order completed');
  };

  const onAbortApplePayOrder = () => {
    // TODO: EMIT EVENT TO PARENT COMPONENT
    setAppError(internalError);
  };

  const {
    start,
    isApplePayReadyOnDevice,
    isPaymentRequestAvailable,
    isApplePaySessionAvailable,
  } = useApplePay(
    formattedAmount.toString(),
    currency,
    requireCustomerInfo,
    isEligibleForCheckout,
    // createApplePayOrder,
    onCompleteApplePayOrder,
    onAbortApplePayOrder,
    sessionProps,
    merchantId,
  );

  const isApplePayEnabledForDevice =
    isApplePayReadyOnDevice && (isApplePaySessionAvailable || isPaymentRequestAvailable);

  const isApplePayEnabled = isApplePayEnabledForDevice && isEligibleForCheckout;

  const handleClickOnApplePayButton = async () => {
    // TODO: EMIT EVENT TO PARENT COMPONENT

    await executeApplePayStart({
      isApplePaySessionAvailable,
      isPaymentRequestAvailable,
      start,
    });
  };


  useEffect(() => {
    if (!companyId) return;

    const appId = `component:${companyId}`;

    featureFlagGateway
      .isEnabled(appId, APPLE_PAY_FOR_INTEGRATION_FLAG)
      .then(setApplePayForIntegrationEnabled)
      .catch((e) => {
        console.log(e);
        setApplePayForIntegrationEnabled(false);
      });
  }, [companyId]);

  return { isApplePayEnabled, isApplePayLoading, handleClickOnApplePayButton };
};
