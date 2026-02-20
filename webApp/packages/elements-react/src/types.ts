import type React from 'react';
import type { InitArgs } from '@conekta/elements';

export type ElementsInitOptions = Omit<InitArgs, 'container'>;

export type ElementsProps = ElementsInitOptions & {
    className?: string;
    style?: React.CSSProperties;
};