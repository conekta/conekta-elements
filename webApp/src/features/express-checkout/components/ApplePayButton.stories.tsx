import type { Meta, StoryObj } from '@storybook/react';
import { ApplePayButton } from './ApplePayButton';

const meta: Meta<typeof ApplePayButton> = {
  title: 'Components/ApplePayButton',
  component: ApplePayButton,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    onPaymentAuthorized: {
      action: 'payment-authorized',
      description: 'Callback function called when Apple Pay payment is authorized with the token result',
    },
    disabled: {
      control: 'boolean',
      description: 'Disables the button',
    },
    loading: {
      control: 'boolean',
      description: 'Shows the loading state',
    },
    variant: {
      control: 'select',
      options: ['black', 'white', 'white-with-line'],
      description: 'Visual variant of the button (for native Apple Pay button only)',
    },
    appearance: {
      control: 'select',
      options: ['light', 'dark', 'auto'],
      description: 'Appearance of the button (affects colors and spinner)',
    },
    borderRadius: {
      control: 'text',
      description: 'Border radius (e.g., "8px", "16px")',
    },
    height: {
      control: 'number',
      description: 'Button height in pixels (minimum: 30px)',
    },
    width: {
      control: 'number',
      description: 'Button width in pixels (minimum: 140px)',
    },
  },
};

export default meta;
type Story = StoryObj<typeof ApplePayButton>;

export const Default: Story = {
  args: {
    onPaymentAuthorized: (result) => console.warn('Apple Pay authorized:', result),
  },
};

export const BlackVariant: Story = {
  args: {
    variant: 'black',
    appearance: 'auto',
    onPaymentAuthorized: (result) => console.warn('Black variant authorized:', result),
  },
};

export const WhiteVariant: Story = {
  args: {
    variant: 'white',
    appearance: 'auto',
    onPaymentAuthorized: (result) => console.warn('White variant authorized:', result),
  },
};

export const WhiteOutlineVariant: Story = {
  args: {
    variant: 'white-with-line',
    appearance: 'auto',
    onPaymentAuthorized: (result) => console.warn('White outline variant authorized:', result),
  },
};

export const LightAppearance: Story = {
  args: {
    appearance: 'light',
    onPaymentAuthorized: (result) => console.warn('Light appearance authorized:', result),
  },
  parameters: {
    backgrounds: { default: 'dark' },
  },
};

export const DarkAppearance: Story = {
  args: {
    appearance: 'dark',
    onPaymentAuthorized: (result) => console.warn('Dark appearance authorized:', result),
  },
};

export const AutoAppearance: Story = {
  args: {
    appearance: 'auto',
    onPaymentAuthorized: (result) => console.warn('Auto appearance authorized:', result),
  },
};

export const Disabled: Story = {
  args: {
    disabled: true,
    onPaymentAuthorized: (result) => console.warn('This should not trigger:', result),
  },
};

export const Loading: Story = {
  args: {
    loading: true,
    onPaymentAuthorized: (result) => console.warn('This should not trigger while loading:', result),
  },
};

export const LoadingLight: Story = {
  args: {
    loading: true,
    appearance: 'light',
    onPaymentAuthorized: (result) => console.warn('This should not trigger while loading:', result),
  },
  parameters: {
    backgrounds: { default: 'dark' },
  },
};

export const SmallSize: Story = {
  args: {
    height: 40,
    width: 160,
    onPaymentAuthorized: (result) => console.warn('Small button authorized:', result),
  },
};

export const LargeSize: Story = {
  args: {
    height: 64,
    width: 300,
    onPaymentAuthorized: (result) => console.warn('Large button authorized:', result),
  },
};

export const MinimumSize: Story = {
  args: {
    height: 30,
    width: 140,
    onPaymentAuthorized: (result) => console.warn('Minimum size button authorized:', result),
  },
};

export const FullWidth: Story = {
  args: {
    width: '100%',
    onPaymentAuthorized: (result) => console.warn('Full width button authorized:', result),
  },
  decorators: [
    (Story) => (
      <div style={{ width: '400px' }}>
        <Story />
      </div>
    ),
  ],
};

export const RoundedCorners: Story = {
  args: {
    borderRadius: '16px',
    onPaymentAuthorized: (result) => console.warn('Rounded corners authorized:', result),
  },
};

export const SharpCorners: Story = {
  args: {
    borderRadius: '0px',
    onPaymentAuthorized: (result) => console.warn('Sharp corners authorized:', result),
  },
};

export const PillShape: Story = {
  args: {
    borderRadius: '28px',
    height: 56,
    onPaymentAuthorized: (result) => console.warn('Pill shape authorized:', result),
  },
};

export const CustomStyled: Story = {
  args: {
    variant: 'black',
    appearance: 'dark',
    borderRadius: '12px',
    height: 60,
    width: 280,
    onPaymentAuthorized: (result) => console.warn('Custom styled authorized:', result),
  },
};

export const CompactButton: Story = {
  args: {
    height: 36,
    width: 150,
    borderRadius: '6px',
    onPaymentAuthorized: (result) => console.warn('Compact button authorized:', result),
  },
};
