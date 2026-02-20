import React, { useEffect, useRef } from 'react';
import type { ElementsProps } from './types';
import { useElements } from './ElementsProvider';

export const ConektaElement: React.FC<ElementsProps> = ({ className, style, ...options }) => {
    const elements = useElements();
    const ref = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        if (!ref.current) return;

        let disposed = false;
        let instance: { destroy: () => Promise<void> } | null = null;

        (async () => {
            instance = await elements.init({
                ...options,
                container: ref.current!,
            });

            if (disposed) {
                await instance.destroy();
                instance = null;
            }
        })();

        return () => {
            disposed = true;
            if (instance) void instance.destroy();
        };
    }, [elements, options]);

    return <div ref={ref} className={className} style={{ width: '100%', ...style }} />;
};