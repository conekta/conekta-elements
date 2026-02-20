import React from 'react';
import { ConektaElements } from './ConektaElements';
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

export const ApplePayButton: React.FC<Props> = (props) => {
    const { checkoutRequestId, locale, theme, onInit, onSuccess, onError, className, style } = props;

    return (
        <ConektaElements
            className={className}
            style={style}
            molecule="applePay"
            checkoutRequestId={checkoutRequestId}
            locale={locale}
            theme={theme}
            onInit={onInit}
            onSuccess={onSuccess}
            onError={onError}
        />
    );
};