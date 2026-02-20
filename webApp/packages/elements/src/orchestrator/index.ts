export { createOrchestrator } from './createOrchestrator';
export type * from './types';

import type { InitArgs } from '../public/types';
import { createElementsRuntime } from '../runtime/createElementsRuntime';
import { registerMolecule } from '../runtime/molecules/register';

export const init = async (args: InitArgs) => {
    registerMolecule(args.molecule);
    const runtime = createElementsRuntime();
    return runtime.init(args);
};