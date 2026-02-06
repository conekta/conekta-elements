import { createContext, ReactNode, useCallback, useContext, useMemo, useState } from 'react';
import { CheckoutStatus } from 'common/constants';
import type { CheckoutRequest } from 'common/interface';
import type { ViewState } from 'common/util/interface';

export interface CheckoutState {
  state: ViewState;
  setState: (state: ViewState) => void;
  updateCheckoutRequest: (checkoutRequest: Partial<CheckoutRequest>) => void;
}

export const initialViewState: ViewState = {
  allowedPaymentMethods: [],
  amountInCurrencyFormat: '',
  checkoutRequest: {
    allowedPaymentMethods: [],
    amount: 0,
    canNotExpire: false,
    companyId: '',
    entityId: '',
    excludeCardNetworks: [],
    expiredAt: 0,
    femsaMigrated: false,
    force3dsFlow: false,
    id: '',
    liveMode: true,
    monthlyInstallmentsEnabled: false,
    monthlyInstallmentsOptions: [],
    name: '',
    needsShippingContact: false,
    openAmount: false,
    plans: [],
    orderTemplate: {
      currency: '',
      customerInfo: { email: '', name: '', phone: '' },
      discountLines: [],
      lineItems: [],
      metadata: [],
      shippingLines: [],
      taxLines: [],
    },
    orders: [],
    paymentKeys: [],
    providers: [],
    quantity: 0,
    recurrent: false,
    redirectionTime: 0,
    returnsControlOn: '',
    slug: '',
    startsAt: 0,
    status: CheckoutStatus.ISSUED,
    type: '',
    url: '',
  },
  device: {},
  entity: {
    allowedPaymentMethods: [],
    conektaLogo: true,
    createdAt: '',
    id: '',
    idReferenceCompany: '',
    msiActive: false,
    name: '',
    status: '',
    threeDs: '',
    notificationEnabled: false,
  },
  formattedAmount: 0,
  isBbva: false,
  isDatalogic: false,
  isHigherThanCashLimitAmount: false,
  isIntegration: false,
  isOutBnplAmountRange: false,
  isPespay: false,
  keys: {
    publicKey: '',
  },
};

const CheckoutFrameContext = createContext<CheckoutState | null>(null);

export const CheckoutFrameProvider = ({
  children,
  initialState,
}: {
  children: ReactNode;
  initialState?: ViewState;
}) => {
  const [state, setInternalState] = useState<ViewState>(initialState ?? initialViewState);

  const setState = useCallback((next: ViewState) => {
    setInternalState(next);
  }, []);

  const updateCheckoutRequest = useCallback((checkoutRequest: Partial<CheckoutRequest>) => {
    setInternalState((prev) => ({
      ...prev,
      checkoutRequest: { ...prev.checkoutRequest, ...checkoutRequest },
    }));
  }, []);

  const value = useMemo<CheckoutState>(
    () => ({
      state,
      setState,
      updateCheckoutRequest,
    }),
    [state, setState, updateCheckoutRequest],
  );

  return <CheckoutFrameContext.Provider value={value}>{children}</CheckoutFrameContext.Provider>;
};

export const useCheckoutFrameStore = (): CheckoutState => {
  const context = useContext(CheckoutFrameContext);
  if (!context) {
    throw new Error('useCheckoutFrameStore must be used within a CheckoutFrameProvider');
  }
  return context;
};

export const useCheckoutFrameContext = (): ViewState => useCheckoutFrameStore().state;

