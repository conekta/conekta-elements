import React from 'react';
import { ConektaElement } from './ConektaElements';
import type { ElementsInitOptions } from './types';

type Props = {
    checkoutRequestId: string;
    locale?: ElementsInitOptions['locale'];
    theme?: ElementsInitOptions['theme'];

    onInit?: ElementsInitOptions['onInit'];
    onSuccess?: ElementsInitOptions['onSuccess'];
    onError?: ElementsInitOptions['onError'];

    className?: string;
    style?: React.CSSProperties;
};

export const ExpressCheckout: React.FC<Props> = ({
    checkoutRequestId,
    locale,
    theme,
    onInit,
    onSuccess,
    onError,
    className,
    style,
}) => {
    return (
        <ConektaElement
            className={className}
            style={style}
            molecule="expressCheckout"
            checkoutRequestId={checkoutRequestId}
            locale={locale}
            theme={theme}
            onInit={onInit}
            onSuccess={onSuccess}
            onError={onError}
        />
    );
};