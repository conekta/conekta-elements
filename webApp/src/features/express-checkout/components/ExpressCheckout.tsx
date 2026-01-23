import { Stack, Group, Text } from '@mantine/core';
import { ApplePayButton } from './ApplePayButton';
import type { ExpressCheckoutProps } from '../types';

export const ExpressCheckout = ({
  publicKey,
  amount,
  currency,
  layout = 'horizontal',
  spacing = 12,
}: ExpressCheckoutProps) => {
  const validatedSpacing = Math.max(8, spacing);

  const handleApplePayClick = () => {
    console.log('Apple Pay clicked', { publicKey, amount, currency });
  };

  const Container = layout === 'horizontal' ? Group : Stack;

  return (
    <Stack gap="md" w="100%">
      <Container gap={validatedSpacing}>
        <ApplePayButton
          onClick={handleApplePayClick}
          appearance="light"
        />
        <ApplePayButton
          onClick={handleApplePayClick}
        />
      </Container>
      <Text size="xs" c="dimmed">
        Amount: {(amount / 100).toFixed(2)} {currency}
      </Text>
    </Stack>
  );
};
