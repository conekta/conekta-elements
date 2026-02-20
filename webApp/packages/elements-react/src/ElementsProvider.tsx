import React, { createContext, useContext } from 'react';
import { ConektaElements } from 'ct-conekta-elements-orchestator-lib';


const Ctx = createContext<typeof ConektaElements | null>(null);

export const ElementsProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    return <Ctx.Provider value={ConektaElements}>{children}</Ctx.Provider>;
};

export const useElements = () => {
    const ctx = useContext(Ctx);
    if (!ctx) throw new Error('useElements must be used within <ElementsProvider>');
    return ctx;
};