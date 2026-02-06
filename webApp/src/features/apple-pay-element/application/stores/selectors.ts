import { checkoutRequestType, SHOPIFY_METADATA_KEYS } from '../../../../common/util/constants';
import { useCheckoutFrameContext } from './CheckoutFrameContext';


export const selectCheckout = () => useCheckoutFrameContext()?.checkoutRequest;

export const selectEntity = () => useCheckoutFrameContext()?.entity;

export const selectOrderTemplate = () => selectCheckout().orderTemplate;

export const selectCurrency = () => selectOrderTemplate().currency;

export const selectAmount = () => selectCheckout().amount;

export const selectCustomerInfo = () => selectCheckout().orderTemplate.customerInfo;

export const selectCheckoutType = () => selectCheckout()?.type;
export const isSubscription = () => selectCheckout()?.plans;

export const selectNeedsShippingContact = () => selectCheckout().needsShippingContact;

export const selectIsShopify = (): boolean =>
  selectCheckout().orderTemplate.metadata?.find((item) => SHOPIFY_METADATA_KEYS.includes(item.key));

export const selectIsHostedPayment = (): boolean => selectCheckout().type === checkoutRequestType.hostedPayment;

export const selecIsPaymentLink = (): boolean => selectCheckout().type === checkoutRequestType.paymentLink;

export const selecIsIntegration = (): boolean => selectCheckout().type === checkoutRequestType.integration;
