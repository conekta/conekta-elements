import { createContext, ReactNode, useCallback, useContext, useMemo, useState } from 'react';

export interface PaymentFormState {
  isLoading: boolean;
  appError?: any; // TODO: Implement IConektaError
  setIsLoading: (isLoading: boolean) => void;
  setAppError: (appError?: any) => void; // TODO: Implement IConektaError
}

const PaymentFormContext = createContext<PaymentFormState | null>(null);

export const PaymentFormProvider = ({
  children,
  initialIsLoading = false,
  initialAppError,
}: {
  children: ReactNode;
  initialIsLoading?: boolean;
  initialAppError?: any; // TODO: Implement IConektaError
}) => {
  const [isLoading, setIsLoadingState] = useState<boolean>(initialIsLoading);
  const [appError, setAppErrorState] = useState<any | undefined>(initialAppError); // TODO: Implement IConektaError

  const setIsLoading = useCallback((next: boolean) => {
    setIsLoadingState(next);
  }, []);

  const setAppError = useCallback((next?: any) => { // TODO: Implement IConektaError
    setAppErrorState(next);
  }, []);

  const value = useMemo<PaymentFormState>(
    () => ({
      isLoading,
      appError,
      setIsLoading,
      setAppError,
    }),
    [isLoading, appError, setIsLoading, setAppError],
  );

  return <PaymentFormContext.Provider value={value}>{children}</PaymentFormContext.Provider>;
};

export const usePaymentFormStore = (): PaymentFormState => {
  const context = useContext(PaymentFormContext);
  if (!context) {
    throw new Error('usePaymentFormStore must be used within a PaymentFormProvider');
  }
  return context;
};
