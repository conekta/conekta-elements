import { Box } from '@mantine/core';
import type { ApplePayButtonProps } from '../types';
import { COLORS, OPACITY, MIN_BUTTON_WIDTH, MIN_BUTTON_HEIGHT } from '../constants/styles';
import { Spinner } from './Spinner';
import { CDN } from '../../../utils/cdn';
import '../styles/applePay.css';

export const ApplePayButton = ({
  onClick = () => {},
  disabled = false,
  loading = false,
  variant = 'black',
  appearance = 'auto',
  borderRadius = '8px',
  height = 56,
  width = 245,
}: ApplePayButtonProps) => {
  const validatedHeight = Math.max(MIN_BUTTON_HEIGHT, height);
  const validatedWidth = typeof width === 'number' ? Math.max(MIN_BUTTON_WIDTH, width) : width;
  const validatedBorderRadius = borderRadius;

  const isDisabled = disabled || loading;
  const opacity = isDisabled ? OPACITY.DISABLED : OPACITY.ENABLED;
  const cursor = isDisabled ? 'not-allowed' : 'pointer';
  const variantClass = `apple-pay-button-${variant}`;

  const backgroundColor = appearance === 'light' ? COLORS.WHITE : COLORS.DARK_BLUE;
  const textColor = appearance === 'light' ? COLORS.BLACK : COLORS.WHITE;
  const shouldHaveHoverEffect = appearance === 'dark' || appearance === 'auto';

  return (
    <Box pos="relative" w={validatedWidth} style={{ minWidth: `${MIN_BUTTON_WIDTH}px` }}>
        {loading && <Spinner color={textColor} />}
        
        <Box
          component="button"
          type="button"
          className={`apple-pay-button ${variantClass}`}
          onClick={onClick}
          disabled={isDisabled}
          data-hover-effect={shouldHaveHoverEffect}
          style={{
            height: `${validatedHeight}px`,
            width: '100%',
            borderRadius: validatedBorderRadius,
            opacity,
            display: 'inline-block',
            transition: shouldHaveHoverEffect ? 'opacity 0.2s ease' : 'none',
            padding: 0,
            border: 'none',
            background: 'transparent',
            cursor,
          }}
        />

        <Box
          component="button"
          type="button"
          className="apple-pay-button-legacy"
          onClick={onClick}
          disabled={isDisabled}
          data-hover-effect={shouldHaveHoverEffect}
          style={{
            height: `${validatedHeight}px`,
            width: '100%',
            borderRadius: validatedBorderRadius,
            opacity,
            backgroundColor,
            color: textColor,
            border: 'none',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontWeight: 600,
            fontSize: '16px',
            transition: shouldHaveHoverEffect ? 'opacity 0.2s ease, transform 0.1s ease' : 'none',
            padding: 0,
            cursor,
          }}
        >
          <img
            src={CDN.Icons.APPLE}
            alt="Apple Pay"
            style={{ 
              visibility: loading ? 'hidden' : 'visible',
              height: '24px',
              filter: appearance === 'light' ? 'invert(1)' : 'none',
            }}
          />
        </Box>
      </Box>
  );
};
