import { Box } from '@mantine/core';
import type { ApplePayButtonProps } from '../types';
import { APPLE_PAY_BUTTON_STYLES } from '../constants/applePay';
import { Spinner } from './Spinner';

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
  const validatedHeight = Math.max(30, height);
  const validatedWidth = typeof width === 'number' ? Math.max(140, width) : width;
  const validatedBorderRadius = borderRadius;

  const isDisabled = disabled || loading;
  const opacity = disabled ? 0.5 : 1;
  const cursor = isDisabled ? 'not-allowed' : 'pointer';
  const variantClass = `apple-pay-button-${variant}`;

  const getBackgroundColor = () => {
    if (appearance === 'light') return '#fff';
    if (appearance === 'dark') return '#1E293B';
    return '#1E293B';
  };

  const getTextColor = () => {
    if (appearance === 'light') return '#000';
    return '#fff';
  };

  const shouldHaveHoverEffect = appearance === 'dark' || appearance === 'auto';

  const handleClick = () => {
    if (!isDisabled) {
      onClick();
    }
  };

  const spinnerColor = appearance === 'light' ? '#000' : '#fff';

  return (
    <>
      <style>{APPLE_PAY_BUTTON_STYLES}</style>

      <Box pos="relative" w={validatedWidth} style={{ minWidth: '140px' }}>
        {loading && <Spinner color={spinnerColor} />}
        
        <Box
          className={`apple-pay-button ${variantClass}`}
          onClick={handleClick}
          style={{
            height: `${validatedHeight}px`,
            width: '100%',
            borderRadius: validatedBorderRadius,
            cursor,
            opacity,
            display: 'inline-block',
            transition: shouldHaveHoverEffect ? 'opacity 0.2s ease' : 'none',
          }}
          onMouseEnter={(e) => {
            if (!isDisabled && shouldHaveHoverEffect) {
              e.currentTarget.style.opacity = '0.8';
            }
          }}
          onMouseLeave={(e) => {
            if (shouldHaveHoverEffect && !loading) {
              e.currentTarget.style.opacity = opacity.toString();
            }
          }}
        />

        <Box
          className="apple-pay-button-legacy"
          onClick={handleClick}
          style={{
            height: `${validatedHeight}px`,
            width: '100%',
            borderRadius: validatedBorderRadius,
            cursor,
            opacity,
            backgroundColor: getBackgroundColor(),
            color: getTextColor(),
            border: 'none',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontWeight: 600,
            fontSize: '16px',
            transition: shouldHaveHoverEffect ? 'opacity 0.2s ease, transform 0.1s ease' : 'none',
          }}
          onMouseEnter={(e) => {
            if (!isDisabled && shouldHaveHoverEffect) {
              e.currentTarget.style.opacity = '0.8';
            }
          }}
          onMouseLeave={(e) => {
            if (shouldHaveHoverEffect && !loading) {
              e.currentTarget.style.opacity = opacity.toString();
            }
          }}
        >
          <Box style={{ 
            visibility: loading ? 'hidden' : 'visible', 
            display: 'flex', 
            alignItems: 'center',
            gap: '6px'
          }}>
            <svg
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill={getTextColor()}
              xmlns="http://www.w3.org/2000/svg"
              style={{ flexShrink: 0 }}
            >
              <path d="M17.05 20.28c-.98.95-2.05.88-3.08.4-1.09-.5-2.08-.48-3.24 0-1.44.62-2.2.44-3.06-.4C2.79 15.25 3.51 7.59 9.05 7.31c1.35.07 2.29.74 3.08.8 1.18-.24 2.31-.93 3.57-.84 1.51.12 2.65.72 3.4 1.8-3.12 1.87-2.38 5.98.48 7.13-.57 1.5-1.31 2.99-2.53 4.09l-.01-.01zM12.03 7.25c-.15-2.23 1.66-4.07 3.74-4.25.29 2.58-2.34 4.5-3.74 4.25z" />
            </svg>
            <span>Pay</span>
          </Box>
        </Box>
      </Box>
    </>
  );
};
