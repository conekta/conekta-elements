import { Stack, Group, Text } from '@mantine/core';
import { ApplePayButton } from './ApplePayButton';
import type { ExpressCheckoutProps, ApplePayTokenResult } from '../types';
import { DEFAULT_LAYOUT, DEFAULT_SPACING, MIN_SPACING } from '../constants/styles';
import { Amount } from 'shared';

export const ExpressCheckout = ({
  publicKey,
  amount,
  currency,
  layout = DEFAULT_LAYOUT,
  spacing = DEFAULT_SPACING,
}: ExpressCheckoutProps) => {
  const validatedSpacing = Math.max(MIN_SPACING, spacing);
  const amountInstance = new Amount(amount);

  const handleApplePayAuthorized = (result: ApplePayTokenResult) => {
    console.warn('Apple Pay authorized', { publicKey, amount, currency, token: result.token });
  };

  const Container = layout === DEFAULT_LAYOUT ? Group : Stack;

  return (
    <Stack gap="md" w="100%">
      <Container gap={validatedSpacing}>
        <ApplePayButton
          onPaymentAuthorized={handleApplePayAuthorized}
          appearance="light"
        />
        <ApplePayButton
          onPaymentAuthorized={handleApplePayAuthorized}
        />
      </Container>
      <Text size="xs" c="dimmed">
        Amount: {amountInstance.toFixed(2)} {currency}
      </Text>
    </Stack>
  );
};
