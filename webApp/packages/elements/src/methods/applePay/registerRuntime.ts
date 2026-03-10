
import { registerMethodModule } from '../../runtime/methods/registry';
import { applePayModule } from './runtimeModule';

export const registerApplePayRuntime = () => registerMethodModule(applePayModule);