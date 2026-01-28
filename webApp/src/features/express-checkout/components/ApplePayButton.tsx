import { Box } from '@mantine/core';
import type { ApplePayButtonProps } from '../types';
import { 
  COLORS, 
  OPACITY, 
  MIN_BUTTON_WIDTH, 
  MIN_BUTTON_HEIGHT,
  DEFAULT_VARIANT,
  DEFAULT_APPEARANCE,
  DEFAULT_BORDER_RADIUS,
  DEFAULT_BUTTON_HEIGHT,
  DEFAULT_BUTTON_WIDTH,
  COMPACT_BUTTON_THRESHOLD,
  MIN_LOGO_HEIGHT,
  LOGO_PADDING,
  STANDARD_LOGO_HEIGHT,
} from '../constants/styles';
import { Spinner } from '../../../shared/components';
import { CDN } from '../../../utils/cdn';
import '../styles/applePay.css';

export const ApplePayButton = ({
  onPaymentAuthorized,
  disabled = false,
  loading = false,
  variant = DEFAULT_VARIANT,
  appearance = DEFAULT_APPEARANCE,
  borderRadius = DEFAULT_BORDER_RADIUS,
  height = DEFAULT_BUTTON_HEIGHT,
  width = DEFAULT_BUTTON_WIDTH,
}: ApplePayButtonProps) => {
  const validatedHeight = Math.max(MIN_BUTTON_HEIGHT, height);
  const validatedWidth = typeof width === 'number' ? Math.max(MIN_BUTTON_WIDTH, width) : width;
  const validatedBorderRadius = borderRadius;

  const isDisabled = disabled || loading;
  const isLightMode = appearance === 'light';
  const isDarkMode = appearance === 'dark' || appearance === 'auto';
  
  const opacity = isDisabled ? OPACITY.DISABLED : OPACITY.ENABLED;
  const cursor = isDisabled ? 'not-allowed' : 'pointer';
  const variantClass = `apple-pay-button-${variant}`;
  const backgroundColor = isLightMode ? COLORS.WHITE : COLORS.BLACK;
  const textColor = isLightMode ? COLORS.BLACK : COLORS.WHITE;
  const shouldHaveHoverEffect = isDarkMode;
  const logoHeight = validatedHeight < COMPACT_BUTTON_THRESHOLD 
    ? Math.max(MIN_LOGO_HEIGHT, validatedHeight - LOGO_PADDING) 
    : STANDARD_LOGO_HEIGHT;

  const handleClick = () => {
    if (!onPaymentAuthorized || isDisabled) return;
    
    const mockTokenResult = {
      token: 'mock_apple_pay_token',
      paymentMethod: {},
    };
    
    void onPaymentAuthorized(mockTokenResult);
  };

  return (
    <Box pos="relative" w={validatedWidth} style={{ minWidth: `${MIN_BUTTON_WIDTH}px` }}>
        {loading && <Spinner color={textColor} />}
        
        <Box
          component="button"
          type="button"
          className={`apple-pay-button ${variantClass}`}
          onClick={handleClick}
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
          onClick={handleClick}
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
              height: `${logoHeight}px`,
              filter: isLightMode ? 'invert(1)' : 'none',
            }}
          />
        </Box>
      </Box>
  );
};
