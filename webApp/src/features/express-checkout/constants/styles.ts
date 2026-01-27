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

export const DEFAULT_BORDER_RADIUS = '8px';
export const DEFAULT_BUTTON_HEIGHT = 56;
export const DEFAULT_BUTTON_WIDTH = 245;

export const COMPACT_BUTTON_THRESHOLD = 40;
export const MIN_LOGO_HEIGHT = 18;
export const LOGO_PADDING = 14;
export const STANDARD_LOGO_HEIGHT = 24;

export const DEFAULT_SPACING = 12;
export const MIN_SPACING = 8;
