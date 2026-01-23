import type { Meta, StoryObj } from '@storybook/react';
import { ApplePayButton } from './ApplePayButton';

// Component configuration for Storybook
const meta: Meta<typeof ApplePayButton> = {
  title: 'Components/ApplePayButton',
  component: ApplePayButton,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    onClick: {
      action: 'clicked',
      description: 'Function to execute when the button is clicked',
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

// Default story
export const Default: Story = {
  args: {
    onClick: () => console.warn('Apple Pay clicked'),
  },
};

// Variants
export const BlackVariant: Story = {
  args: {
    variant: 'black',
    appearance: 'auto',
    onClick: () => console.warn('Black variant clicked'),
  },
};

export const WhiteVariant: Story = {
  args: {
    variant: 'white',
    appearance: 'auto',
    onClick: () => console.warn('White variant clicked'),
  },
};

export const WhiteOutlineVariant: Story = {
  args: {
    variant: 'white-with-line',
    appearance: 'auto',
    onClick: () => console.warn('White outline variant clicked'),
  },
};

// Appearances
export const LightAppearance: Story = {
  args: {
    appearance: 'light',
    onClick: () => console.warn('Light appearance clicked'),
  },
  parameters: {
    backgrounds: { default: 'dark' },
  },
};

export const DarkAppearance: Story = {
  args: {
    appearance: 'dark',
    onClick: () => console.warn('Dark appearance clicked'),
  },
};

export const AutoAppearance: Story = {
  args: {
    appearance: 'auto',
    onClick: () => console.warn('Auto appearance clicked'),
  },
};

// States
export const Disabled: Story = {
  args: {
    disabled: true,
    onClick: () => console.warn('This should not trigger'),
  },
};

export const Loading: Story = {
  args: {
    loading: true,
    onClick: () => console.warn('This should not trigger while loading'),
  },
};

export const LoadingLight: Story = {
  args: {
    loading: true,
    appearance: 'light',
    onClick: () => console.warn('This should not trigger while loading'),
  },
  parameters: {
    backgrounds: { default: 'dark' },
  },
};

// Sizes
export const SmallSize: Story = {
  args: {
    height: 40,
    width: 160,
    onClick: () => console.warn('Small button clicked'),
  },
};

export const LargeSize: Story = {
  args: {
    height: 64,
    width: 300,
    onClick: () => console.warn('Large button clicked'),
  },
};

export const MinimumSize: Story = {
  args: {
    height: 30,
    width: 140,
    onClick: () => console.warn('Minimum size button clicked'),
  },
};

export const FullWidth: Story = {
  args: {
    width: '100%',
    onClick: () => console.warn('Full width button clicked'),
  },
  decorators: [
    (Story) => (
      <div style={{ width: '400px' }}>
        <Story />
      </div>
    ),
  ],
};

// Border radius
export const RoundedCorners: Story = {
  args: {
    borderRadius: '16px',
    onClick: () => console.warn('Rounded corners clicked'),
  },
};

export const SharpCorners: Story = {
  args: {
    borderRadius: '0px',
    onClick: () => console.warn('Sharp corners clicked'),
  },
};

export const PillShape: Story = {
  args: {
    borderRadius: '28px',
    height: 56,
    onClick: () => console.warn('Pill shape clicked'),
  },
};

// Special combinations
export const CustomStyled: Story = {
  args: {
    variant: 'black',
    appearance: 'dark',
    borderRadius: '12px',
    height: 60,
    width: 280,
    onClick: () => console.warn('Custom styled clicked'),
  },
};

export const CompactButton: Story = {
  args: {
    height: 36,
    width: 150,
    borderRadius: '6px',
    onClick: () => console.warn('Compact button clicked'),
  },
};
