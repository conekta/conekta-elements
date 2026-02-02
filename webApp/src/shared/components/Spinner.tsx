import { Loader, Box } from '@mantine/core';
import { CDNResources } from 'shared';

const resources = CDNResources.getInstance();

interface SpinnerProps {
  color?: string;
}

export const Spinner = ({ color = resources.Colors.WHITE }: SpinnerProps) => {
  return (
    <Box
      pos="absolute"
      style={{
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)',
        zIndex: 10,
      }}
    >
      <Loader color={color} size="sm" type="oval" />
    </Box>
  );
};
