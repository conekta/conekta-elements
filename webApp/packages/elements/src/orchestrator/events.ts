type Handler<T> = (payload: T) => void;

export type OrchestratorEvents = {
    'element:ready': { method: string; data: any };
    'element:state': { method: string; data: any };
    'element:action_required': { method: string; data: any };
    'element:result': { method: string; data: any };
    'element:lifecycle_event': { method: string; data: any };
    'orchestrator:error': { method?: string; error: any };
};

export const createEventBus = () => {
    const handlers = new Map<keyof OrchestratorEvents, Set<Handler<any>>>();

    function on<K extends keyof OrchestratorEvents>(event: K, handler: Handler<OrchestratorEvents[K]>) {
        const set = handlers.get(event) ?? new Set();
        set.add(handler);
        handlers.set(event, set);
        return () => off(event, handler);
    }

    function off<K extends keyof OrchestratorEvents>(event: K, handler: Handler<OrchestratorEvents[K]>) {
        handlers.get(event)?.delete(handler);
    }

    function emit<K extends keyof OrchestratorEvents>(event: K, payload: OrchestratorEvents[K]) {
        handlers.get(event)?.forEach((h) => h(payload));
    }

    return { on, off, emit };
};
