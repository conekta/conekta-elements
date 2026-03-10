import { registerMethodModule } from '../../runtime/methods/registry';
import { googlePayModule } from './runtimeModule';

export const registerGooglePayRuntime = () => registerMethodModule(googlePayModule);