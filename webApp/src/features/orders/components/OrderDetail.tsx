import { useState } from 'react';
import { Stack, Text, Button, Card, TextInput, Alert, Loader, Group } from '@mantine/core';
import { useConektaClient } from '../../../providers/ConektaProvider';

interface OrderData {
  id: string;
  liveMode: boolean;
  amount: bigint;
  currency: string;
  paymentStatus: string;
  customerInfo: {
    name: string;
    email: string;
    phone: string;
  } | null;
}

export const OrderDetail = () => {
  const client = useConektaClient();
  const [orderId, setOrderId] = useState('');
  const [order, setOrder] = useState<OrderData | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleFetchOrder = async () => {
    if (!orderId.trim()) return;

    setLoading(true);
    setError(null);
    setOrder(null);

    try {
      const result = await client.getOrder(orderId);
      setOrder(result as unknown as OrderData);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error fetching order');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Stack gap="md" w="100%" maw={480}>
      <Group align="end">
        <TextInput
          label="Order ID"
          placeholder="ord_2tNHGpFftXhXXx1zP"
          value={orderId}
          onChange={(e) => setOrderId(e.currentTarget.value)}
          style={{ flex: 1 }}
        />
        <Button onClick={handleFetchOrder} loading={loading}>
          Fetch
        </Button>
      </Group>

      {error && (
        <Alert color="red" title="Error">
          {error}
        </Alert>
      )}

      {loading && <Loader size="sm" />}

      {order && (
        <Card shadow="sm" padding="lg" radius="md" withBorder>
          <Stack gap="xs">
            <Text fw={700} size="lg">Order {order.id}</Text>
            <Text size="sm" c="dimmed">Status: {order.paymentStatus}</Text>
            <Text size="sm">Amount: {order.amount.toString()} {order.currency}</Text>
            <Text size="sm">Live mode: {order.liveMode ? 'Yes' : 'No'}</Text>
            {order.customerInfo && (
              <>
                <Text size="sm" fw={500} mt="xs">Customer</Text>
                <Text size="sm">{order.customerInfo.name}</Text>
                <Text size="sm" c="dimmed">{order.customerInfo.email}</Text>
                <Text size="sm" c="dimmed">{order.customerInfo.phone}</Text>
              </>
            )}
          </Stack>
        </Card>
      )}
    </Stack>
  );
};
