import { describe, it, expect, vi, beforeEach, type Mock } from 'vitest';
import { render } from '@testing-library/react';
import { userEvent } from '@testing-library/user-event';
import { MantineProvider } from '@mantine/core';
import { ApplePayButton } from '../../src/features/express-checkout/components/ApplePayButton';
import type { ApplePayTokenResult } from '../../src/features/express-checkout/types';
import {
  COLORS,
  OPACITY,
  MIN_BUTTON_WIDTH,
  MIN_BUTTON_HEIGHT,
  DEFAULT_VARIANT,
  DEFAULT_BORDER_RADIUS,
  MIN_LOGO_HEIGHT,
  LOGO_PADDING,
  STANDARD_LOGO_HEIGHT,
} from '../../src/features/express-checkout/constants/styles';

// Helper to render with Mantine Provider
const renderWithMantine = (ui: React.ReactElement) => {
  return render(<MantineProvider>{ui}</MantineProvider>);
};

describe('ApplePayButton', () => {
  let mockOnPaymentAuthorized: Mock<(result: ApplePayTokenResult) => void | Promise<void>>;

  beforeEach(() => {
    mockOnPaymentAuthorized = vi.fn();
  });

  // ========================================
  // CASE 1: UNIT TESTS
  // ========================================

  describe('Rendering and Default Props', () => {
    it('should render correctly', () => {
      const { container } = renderWithMantine(<ApplePayButton />);
      const buttons = container.querySelectorAll('button');
      expect(buttons).toHaveLength(2); // Standard and legacy buttons
    });

    it('should use default values when no props are passed', () => {
      const { container } = renderWithMantine(<ApplePayButton />);
      const button = container.querySelector('.apple-pay-button');

      expect(button).toBeInTheDocument();
      expect(button).toHaveClass(`apple-pay-button-${DEFAULT_VARIANT}`);
    });

    it('should render both buttons (standard and legacy) in the DOM', () => {
      const { container } = renderWithMantine(<ApplePayButton />);

      const standardButton = container.querySelector('.apple-pay-button');
      const legacyButton = container.querySelector('.apple-pay-button-legacy');

      expect(standardButton).toBeInTheDocument();
      expect(legacyButton).toBeInTheDocument();
    });
  });

  describe('Dimension Validation', () => {
    it('should not allow height less than MIN_BUTTON_HEIGHT', () => {
      const { container } = renderWithMantine(
        <ApplePayButton height={20} />
      );
      const button = container.querySelector('.apple-pay-button');

      expect(button).toHaveStyle({ height: `${MIN_BUTTON_HEIGHT}px` });
    });

    it('should not allow width less than MIN_BUTTON_WIDTH', () => {
      const { container } = renderWithMantine(
        <ApplePayButton width={50} />
      );
      const wrapper = container.querySelector('[style*="min-width"]');

      expect(wrapper).toHaveStyle({ minWidth: `${MIN_BUTTON_WIDTH}px` });
    });

    it('should calculate logoHeight correctly when height < COMPACT_BUTTON_THRESHOLD', () => {
      const smallHeight = 35;
      const { container } = renderWithMantine(
        <ApplePayButton height={smallHeight} />
      );

      const logo = container.querySelector('img') as HTMLImageElement;
      const expectedLogoHeight = Math.max(MIN_LOGO_HEIGHT, smallHeight - LOGO_PADDING);

      expect(logo).toHaveStyle({ height: `${expectedLogoHeight}px` });
    });

    it('should use STANDARD_LOGO_HEIGHT when height >= COMPACT_BUTTON_THRESHOLD', () => {
      const { container } = renderWithMantine(
        <ApplePayButton height={56} />
      );

      const logo = container.querySelector('img') as HTMLImageElement;

      expect(logo).toHaveStyle({ height: `${STANDARD_LOGO_HEIGHT}px` });
    });

    it('should accept width as string', () => {
      const { container } = renderWithMantine(
        <ApplePayButton width="100%" />
      );

      const wrapper = container.firstChild as HTMLElement;
      expect(wrapper).toBeInTheDocument();
    });
  });

  describe('Component States', () => {
    it('disabled state: should have not-allowed cursor and reduced opacity', () => {
      const { container } = renderWithMantine(
        <ApplePayButton disabled={true} />
      );
      const button = container.querySelector('.apple-pay-button');

      expect(button).toHaveStyle({
        cursor: 'not-allowed',
        opacity: OPACITY.DISABLED.toString(),
      });
      expect(button).toBeDisabled();
    });

    it('loading state: should show spinner and disable button', () => {
      const { container } = renderWithMantine(
        <ApplePayButton loading={true} />
      );
      const button = container.querySelector('.apple-pay-button');
      const logo = container.querySelector('img');

      expect(button).toBeDisabled();
      expect(logo).toHaveStyle({ visibility: 'hidden' });
    });

    it('enabled state: should have pointer cursor and full opacity', () => {
      const { container } = renderWithMantine(
        <ApplePayButton disabled={false} loading={false} />
      );
      const button = container.querySelector('.apple-pay-button');

      expect(button).toHaveStyle({
        cursor: 'pointer',
        opacity: OPACITY.ENABLED.toString(),
      });
      expect(button).not.toBeDisabled();
    });

    it('loading should take priority over disabled in final state', () => {
      const { container } = renderWithMantine(
        <ApplePayButton loading={true} disabled={false} />
      );
      const button = container.querySelector('.apple-pay-button');

      expect(button).toBeDisabled();
    });
  });

  describe('Appearances', () => {
    it('light appearance: should have white background and inverted logo', () => {
      const { container } = renderWithMantine(
        <ApplePayButton appearance="light" />
      );

      const legacyButton = container.querySelector('.apple-pay-button-legacy');
      const logo = container.querySelector('img');

      expect(legacyButton).toHaveStyle({ backgroundColor: COLORS.WHITE });
      expect(logo).toHaveStyle({ filter: 'invert(1)' });
    });

    it('dark appearance: should have black background and no filter on logo', () => {
      const { container } = renderWithMantine(
        <ApplePayButton appearance="dark" />
      );

      const legacyButton = container.querySelector('.apple-pay-button-legacy');
      const logo = container.querySelector('img');

      expect(legacyButton).toHaveStyle({ backgroundColor: COLORS.BLACK });
      expect(logo).toHaveStyle({ filter: 'none' });
    });

    it('auto appearance: should behave like dark', () => {
      const { container } = renderWithMantine(
        <ApplePayButton appearance="auto" />
      );

      const legacyButton = container.querySelector('.apple-pay-button-legacy');
      const standardButton = container.querySelector('.apple-pay-button');

      expect(legacyButton).toHaveStyle({ backgroundColor: COLORS.BLACK });
      expect(standardButton).toHaveAttribute('data-hover-effect', 'true');
    });

    it('should apply transitions only in dark mode', () => {
      const { container: darkContainer } = renderWithMantine(
        <ApplePayButton appearance="dark" />
      );
      const { container: lightContainer } = renderWithMantine(
        <ApplePayButton appearance="light" />
      );

      const darkButton = darkContainer.querySelector('.apple-pay-button');
      const lightButton = lightContainer.querySelector('.apple-pay-button');

      expect(darkButton).toHaveStyle({ transition: 'opacity 0.2s ease' });
      expect(lightButton).toHaveStyle({ transition: 'none' });
    });
  });

  describe('Interactions', () => {
    it('should call onPaymentAuthorized with mockTokenResult when enabled', async () => {
      const user = userEvent.setup();
      const { container } = renderWithMantine(
        <ApplePayButton onPaymentAuthorized={mockOnPaymentAuthorized} />
      );

      const button = container.querySelector('.apple-pay-button') as HTMLButtonElement;
      await user.click(button);

      expect(mockOnPaymentAuthorized).toHaveBeenCalledTimes(1);
      expect(mockOnPaymentAuthorized).toHaveBeenCalledWith({
        token: 'mock_apple_pay_token',
        paymentMethod: {},
      });
    });

    it('should not execute callback when disabled', async () => {
      const user = userEvent.setup();
      const { container } = renderWithMantine(
        <ApplePayButton
          onPaymentAuthorized={mockOnPaymentAuthorized}
          disabled={true}
        />
      );

      const button = container.querySelector('.apple-pay-button') as HTMLButtonElement;
      await user.click(button);

      expect(mockOnPaymentAuthorized).not.toHaveBeenCalled();
    });

    it('should not execute callback when loading', async () => {
      const user = userEvent.setup();
      const { container } = renderWithMantine(
        <ApplePayButton
          onPaymentAuthorized={mockOnPaymentAuthorized}
          loading={true}
        />
      );

      const button = container.querySelector('.apple-pay-button') as HTMLButtonElement;
      await user.click(button);

      expect(mockOnPaymentAuthorized).not.toHaveBeenCalled();
    });

    it('should not fail if onPaymentAuthorized is not defined', async () => {
      const user = userEvent.setup();
      const { container } = renderWithMantine(<ApplePayButton />);

      const button = container.querySelector('.apple-pay-button') as HTMLButtonElement;

      await expect(user.click(button)).resolves.not.toThrow();
    });

    it('should verify mock token has correct structure', async () => {
      const user = userEvent.setup();
      const { container } = renderWithMantine(
        <ApplePayButton onPaymentAuthorized={mockOnPaymentAuthorized} />
      );

      const button = container.querySelector('.apple-pay-button') as HTMLButtonElement;
      await user.click(button);

      const calledWith = mockOnPaymentAuthorized.mock.calls[0][0];

      expect(calledWith).toHaveProperty('token');
      expect(calledWith).toHaveProperty('paymentMethod');
      expect(typeof calledWith.token).toBe('string');
    });
  });

  describe('Dynamic Styles', () => {
    it('should apply borderRadius correctly', () => {
      const customRadius = '12px';
      const { container } = renderWithMantine(
        <ApplePayButton borderRadius={customRadius} />
      );

      const button = container.querySelector('.apple-pay-button');
      expect(button).toHaveStyle({ borderRadius: customRadius });
    });

    it('should apply default borderRadius', () => {
      const { container } = renderWithMantine(<ApplePayButton />);

      const button = container.querySelector('.apple-pay-button');
      expect(button).toHaveStyle({ borderRadius: DEFAULT_BORDER_RADIUS });
    });

    it('should apply variant class correctly', () => {
      const variants: Array<'black' | 'white' | 'white-with-line'> = ['black', 'white', 'white-with-line'];

      variants.forEach(variant => {
        const { container } = renderWithMantine(
          <ApplePayButton variant={variant} />
        );

        const button = container.querySelector('.apple-pay-button');
        expect(button).toHaveClass(`apple-pay-button-${variant}`);
      });
    });

    it('should apply custom height and width', () => {
      const customHeight = 60;
      const customWidth = 300;

      const { container } = renderWithMantine(
        <ApplePayButton height={customHeight} width={customWidth} />
      );

      const button = container.querySelector('.apple-pay-button');
      expect(button).toHaveStyle({ height: `${customHeight}px` });
    });
  });

  // ========================================
  // CASE 2: ACCESSIBILITY TESTS
  // ========================================

  describe('Accessibility (a11y)', () => {
    it('button should have type="button"', () => {
      const { container } = renderWithMantine(<ApplePayButton />);
      const buttons = container.querySelectorAll('button');

      buttons.forEach(button => {
        expect(button).toHaveAttribute('type', 'button');
      });
    });

    it('should have disabled attribute when appropriate', () => {
      const { container } = renderWithMantine(
        <ApplePayButton disabled={true} />
      );

      const button = container.querySelector('.apple-pay-button');
      expect(button).toHaveAttribute('disabled');
    });

    it('should have alt text on logo image', () => {
      const { container } = renderWithMantine(<ApplePayButton />);
      const logo = container.querySelector('img');

      expect(logo).toHaveAttribute('alt', 'Apple Pay');
    });

    it('should be keyboard navigable when enabled', () => {
      const { container } = renderWithMantine(<ApplePayButton />);
      const button = container.querySelector('.apple-pay-button') as HTMLButtonElement;

      // Button elements are focusable by default (no tabindex="-1" attribute)
      expect(button.getAttribute('tabindex')).not.toBe('-1');
      expect(button.tagName.toLowerCase()).toBe('button');
    });

    it('should maintain accessibility with different states', () => {
      const { container: disabledContainer } = renderWithMantine(
        <ApplePayButton disabled={true} />
      );
      const { container: loadingContainer } = renderWithMantine(
        <ApplePayButton loading={true} />
      );

      const disabledButton = disabledContainer.querySelector('.apple-pay-button');
      const loadingButton = loadingContainer.querySelector('.apple-pay-button');

      expect(disabledButton).toHaveAttribute('disabled');
      expect(loadingButton).toHaveAttribute('disabled');
    });

    it('buttons should be focusable when not disabled', () => {
      const { container } = renderWithMantine(<ApplePayButton />);
      const button = container.querySelector('.apple-pay-button') as HTMLButtonElement;

      button.focus();
      expect(document.activeElement).toBe(button);
    });

    it('should be activatable with keyboard (Enter/Space)', async () => {
      const user = userEvent.setup();
      const { container } = renderWithMantine(
        <ApplePayButton onPaymentAuthorized={mockOnPaymentAuthorized} />
      );

      const button = container.querySelector('.apple-pay-button') as HTMLButtonElement;
      button.focus();

      await user.keyboard('{Enter}');
      expect(mockOnPaymentAuthorized).toHaveBeenCalled();
    });
  });

  // ========================================
  // CASE 4: SNAPSHOT TESTS
  // ========================================

  describe('Snapshots', () => {
    it('should match snapshot for variant "black"', () => {
      const { container } = renderWithMantine(
        <ApplePayButton variant="black" />
      );
      expect(container.firstChild).toMatchSnapshot();
    });

    it('should match snapshot for variant "white"', () => {
      const { container } = renderWithMantine(
        <ApplePayButton variant="white" />
      );
      expect(container.firstChild).toMatchSnapshot();
    });

    it('should match snapshot for variant "white-with-line"', () => {
      const { container } = renderWithMantine(
        <ApplePayButton variant="white-with-line" />
      );
      expect(container.firstChild).toMatchSnapshot();
    });

    it('should match snapshot for appearance "light"', () => {
      const { container } = renderWithMantine(
        <ApplePayButton appearance="light" />
      );
      expect(container.firstChild).toMatchSnapshot();
    });

    it('should match snapshot for appearance "dark"', () => {
      const { container } = renderWithMantine(
        <ApplePayButton appearance="dark" />
      );
      expect(container.firstChild).toMatchSnapshot();
    });

    it('should match snapshot for appearance "auto"', () => {
      const { container } = renderWithMantine(
        <ApplePayButton appearance="auto" />
      );
      expect(container.firstChild).toMatchSnapshot();
    });

    it('should match snapshot in disabled state', () => {
      const { container } = renderWithMantine(
        <ApplePayButton disabled={true} />
      );
      expect(container.firstChild).toMatchSnapshot();
    });

    it('should match snapshot in loading state', () => {
      const { container } = renderWithMantine(
        <ApplePayButton loading={true} />
      );
      expect(container.firstChild).toMatchSnapshot();
    });

    it('should match snapshot with all custom props', () => {
      const { container } = renderWithMantine(
        <ApplePayButton
          variant="white-with-line"
          appearance="light"
          height={60}
          width={300}
          borderRadius="16px"
          disabled={false}
          loading={false}
          onPaymentAuthorized={mockOnPaymentAuthorized}
        />
      );
      expect(container.firstChild).toMatchSnapshot();
    });

    it('should match snapshot with minimum dimensions', () => {
      const { container } = renderWithMantine(
        <ApplePayButton height={MIN_BUTTON_HEIGHT} width={MIN_BUTTON_WIDTH} />
      );
      expect(container.firstChild).toMatchSnapshot();
    });
  });
});
