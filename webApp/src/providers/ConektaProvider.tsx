import { ReactNode } from 'react';

interface ConektaProviderProps {
  publicKey: string;
  children: ReactNode;
}

export const ConektaProvider = ({ children }: ConektaProviderProps) => {
  return <>{children}</>;
};
