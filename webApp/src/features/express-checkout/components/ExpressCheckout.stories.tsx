import type { Meta, StoryObj } from '@storybook/react';
import { ExpressCheckout } from './ExpressCheckout';

// Configuración del componente para Storybook
const meta: Meta<typeof ExpressCheckout> = {
  title: 'Components/ExpressCheckout',
  component: ExpressCheckout,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    amount: {
      control: 'number',
      description: 'Monto a pagar',
    },
    currency: {
      control: 'text',
      description: 'Código de moneda',
    },
    publicKey: {
      control: 'text',
      description: 'Clave pública para el gateway de pago',
    },
  },
};

export default meta;
type Story = StoryObj<typeof ExpressCheckout>;

// Historia por defecto
export const Default: Story = {
  args: {
    publicKey: 'pk_test_123456789',
    amount: 100,
    currency: 'USD',
  },
};

// Historia con monto alto
export const HighAmount: Story = {
  args: {
    publicKey: 'pk_test_123456789',
    amount: 10000,
    currency: 'USD',
  },
};

// Historia con diferentes monedas
export const EurosCurrency: Story = {
  args: {
    publicKey: 'pk_test_123456789',
    amount: 250,
    currency: 'EUR',
  },
};

export const PesosCurrency: Story = {
  args: {
    publicKey: 'pk_test_123456789',
    amount: 5000,
    currency: 'COP',
  },
};

// Historia con monto mínimo
export const MinimumAmount: Story = {
  args: {
    publicKey: 'pk_test_123456789',
    amount: 1,
    currency: 'USD',
  },
};