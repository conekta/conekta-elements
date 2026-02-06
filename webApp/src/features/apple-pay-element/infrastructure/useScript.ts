import { useState } from 'react';

export type TUseScriptStatus = 'idle' | 'loading' | 'ready' | 'error';

export interface IUseScriptOptions {
  shouldPreventLoad?: boolean;
  removeOnUnmount?: boolean;
  crossOrigin?: string;
  cachedScriptStatus?: TUseScriptStatus;
}

const cachedScriptStatuses: Record<string, TUseScriptStatus | undefined> = {};

function getScriptNode(src: string): { node: HTMLScriptElement | null; status: TUseScriptStatus | undefined } {
  const node: HTMLScriptElement | null = document.querySelector(`script[src="${src}"]`);
  const status = node?.getAttribute('data-status') as TUseScriptStatus | undefined;

  return {
    node,
    status,
  };
}

const createScriptNode = (src: string, options?: IUseScriptOptions): HTMLScriptElement => {
  const { crossOrigin } = options ?? {};
  const scriptNode = document.createElement('script');

  scriptNode.src = src;
  scriptNode.async = true;
  scriptNode.setAttribute('data-status', 'loading');
  if (crossOrigin) {
    scriptNode.crossOrigin = crossOrigin;
  }
  document.body.appendChild(scriptNode);

  const setAttributeFromEvent = (event: Event): void => {
    const scriptStatus: TUseScriptStatus = event.type === 'load' ? 'ready' : 'error';

    scriptNode?.setAttribute('data-status', scriptStatus);
  };

  scriptNode.addEventListener('load', setAttributeFromEvent);
  scriptNode.addEventListener('error', setAttributeFromEvent);

  return scriptNode;
};

export function useScript(options?: IUseScriptOptions): {
  status: TUseScriptStatus;
  loadScript: (src: string) => void;
} {
  const [status, setStatus] = useState<TUseScriptStatus>('idle');

  const loadScript = (src: string): void | (() => void) => {
    if (!src || options?.shouldPreventLoad) {
      return;
    }

    const cachedScriptStatus = cachedScriptStatuses[src];
    if (cachedScriptStatus === 'ready' || cachedScriptStatus === 'error') {
      setStatus(cachedScriptStatus);
      return;
    }

    const scriptNode = getOrCreateScriptNode(src, { ...options, cachedScriptStatus });
    const setStateFromEvent = createSetStateFromEvent(src);

    scriptNode.addEventListener('load', setStateFromEvent);
    scriptNode.addEventListener('error', setStateFromEvent);

    return createCleanupFunction(scriptNode, src);
  };

  const getOrCreateScriptNode = (src: string, options?: IUseScriptOptions): HTMLScriptElement => {
    const { cachedScriptStatus } = options ?? {};
    const script = getScriptNode(src);
    let scriptNode = script.node;

    if (!scriptNode) {
      scriptNode = createScriptNode(src, options);
    } else {
      setStatus(script.status ?? cachedScriptStatus ?? 'loading');
    }

    return scriptNode;
  };

  const createSetStateFromEvent =
    (src: string) =>
    (event: Event): void => {
      const newStatus = event.type === 'load' ? 'ready' : 'error';
      setStatus(newStatus);
      cachedScriptStatuses[src] = newStatus;
    };

  const createCleanupFunction = (scriptNode: HTMLScriptElement, src: string) => (): void => {
    if (scriptNode) {
      scriptNode.removeEventListener('load', createSetStateFromEvent(src));
      scriptNode.removeEventListener('error', createSetStateFromEvent(src));
    }

    if (scriptNode && options?.removeOnUnmount) {
      scriptNode.remove();
    }
  };

  return { loadScript, status };
}
