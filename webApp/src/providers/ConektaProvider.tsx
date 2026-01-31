import { createContext, useContext, useMemo, ReactNode } from 'react';
import { ConektaJsClient } from 'shared';

interface ConektaContextValue {
  client: ConektaJsClient;
}

const ConektaContext = createContext<ConektaContextValue | null>(null);

interface ConektaProviderProps {
  publicKey: string;
  language?: string;
  apiVersion?: string;
  children: ReactNode;
}

export const ConektaProvider = ({
  publicKey,
  language,
  apiVersion,
  children,
}: ConektaProviderProps) => {
  const value = useMemo<ConektaContextValue>(() => {
    const args = [publicKey];
    if (language) args.push(language);
    if (apiVersion) args.push(apiVersion);
    return {
      client: new (ConektaJsClient as any)(...args),
    };
  }, [publicKey, language, apiVersion]);

  return (
    <ConektaContext.Provider value={value}>{children}</ConektaContext.Provider>
  );
};

export const useConektaClient = (): ConektaJsClient => {
  const context = useContext(ConektaContext);
  if (!context) {
    throw new Error('useConektaClient must be used within a ConektaProvider');
  }
  return context.client;
};
