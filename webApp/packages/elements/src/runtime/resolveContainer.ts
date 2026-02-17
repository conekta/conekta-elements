export const resolveContainer = (target: string | HTMLElement): HTMLElement => {
    if (typeof target !== 'string') return target;
    const el = document.querySelector(target);
    if (!el) throw new Error(`Container not found: ${target}`);
    return el as HTMLElement;
};
