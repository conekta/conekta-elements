// Re-export constants from shared module
import { CDNResources } from 'shared';

const resources = CDNResources.getInstance();

export const COLORS = resources.Colors;
export const OPACITY = resources.Opacity;
export const MIN_BUTTON_WIDTH = resources.ButtonSizes.MIN_BUTTON_WIDTH;
export const MIN_BUTTON_HEIGHT = resources.ButtonSizes.MIN_BUTTON_HEIGHT;

export const DEFAULT_VARIANT = 'black' as const;
export const DEFAULT_APPEARANCE = 'auto' as const;
export const DEFAULT_LAYOUT = 'horizontal' as const;
