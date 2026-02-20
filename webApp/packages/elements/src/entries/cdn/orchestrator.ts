import { ConektaElements } from '../../public';

declare global {
    interface Window {
        ConektaElements?: typeof ConektaElements;
    }
}

window.ConektaElements = ConektaElements;
