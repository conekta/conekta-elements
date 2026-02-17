import type { InitArgs } from './types.ts';
import { createElementsRuntime } from '../runtime/createElementsRuntime.ts';

export const ConektaElements = {
    init: (args: InitArgs) => {
        const runtime = createElementsRuntime();
        return runtime.init(args);
    },
} as const;


export type { InitArgs };
