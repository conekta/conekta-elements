import type { InitArgs } from './types.ts';
import { createElementsRuntime } from '../runtime/createElementsRuntime.ts';
import { registerMolecule } from '../runtime/molecules/register.ts';

const runtime = createElementsRuntime();

export const ConektaElements = {
    init: (args: InitArgs) => {
        registerMolecule(args.molecule);
        return runtime.init(args);
    },
} as const;


export type { InitArgs };
