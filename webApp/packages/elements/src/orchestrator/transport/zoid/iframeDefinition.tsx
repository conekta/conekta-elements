import type { ElementRPC } from '../../../shared/types';
import type { ViewState } from '../../../shared/types';

const dimensions = { height: '60px', width: '100%' };

export const iframeDefinition = {
  tag: '',
  url: '',
  props: {
    locale: { type: 'string', required: false },
    theme: { type: 'object', required: false },

    onReady: { type: 'function', required: false },
    onStateChange: { type: 'function', required: false },
    onActionRequired: { type: 'function', required: false },
    onLifecycleEvent: { type: 'function', required: false },
    onResult: { type: 'function', required: false },
    onLog: { type: 'function', required: false },
  },

  dimensions,
  attributes: { iframe: { allow: 'payment *' } },
  autoResize: { height: true, width: false },

  exports: ({ getExports }: any) => {
    const call = <K extends keyof ElementRPC>(
      name: K,
      ...args: Parameters<ElementRPC[K]>
    ): ReturnType<ElementRPC[K]> => {
      return getExports().then((exp: any) => {
        if (!exp || typeof exp[name] !== 'function') {
          throw new Error(`Child did not export RPC method: ${String(name)}`);
        }
        return exp[name](...args);
      });
    };

    return {
      submit: () => call('submit'),
      reset: () => call('reset'),
      setViewState: (v: ViewState) => call('setViewState', v),
      setActive: (a: boolean) => call('setActive', a),
      destroy: () => call('destroy'),
    } satisfies ElementRPC;
  },
}