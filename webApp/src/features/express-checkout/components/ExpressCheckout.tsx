import { Stack, Group, Text } from '@mantine/core';
import { ApplePayButton } from './ApplePayButton';
import type { ExpressCheckoutProps, ApplePayTokenResult } from '../types';
import { DEFAULT_LAYOUT, DEFAULT_SPACING, MIN_SPACING } from '../constants/styles';

export const ExpressCheckout = ({
  publicKey,
  amount,
  currency,
  layout = DEFAULT_LAYOUT,
  spacing = DEFAULT_SPACING,
}: ExpressCheckoutProps) => {
  const validatedSpacing = Math.max(MIN_SPACING, spacing);

  const handleApplePayAuthorized = async (result: ApplePayTokenResult) => {
    // TODO: Send token to backend for processing
    console.log('Apple Pay authorized', { publicKey, amount, currency, token: result.token });
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
        Amount: {(amount / 100).toFixed(2)} {currency}
      </Text>
    </Stack>
  );
};
