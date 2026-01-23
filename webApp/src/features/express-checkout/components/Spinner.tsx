import { Box } from '@mantine/core';

interface SpinnerProps {
  color?: string;
}

export const Spinner = ({ color = '#fff' }: SpinnerProps) => {
  return (
    <>
      <style>
        {`
          @keyframes spin {
            from {
              transform: rotate(0deg);
            }
            to {
              transform: rotate(360deg);
            }
          }
        `}
      </style>
      
      <Box
        pos="absolute"
        style={{
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
          zIndex: 10,
        }}
      >
        <svg
          width="24"
          height="24"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          style={{
            animation: 'spin 1s linear infinite',
          }}
        >
          <circle
            cx="12"
            cy="12"
            r="10"
            stroke={color}
            strokeWidth="3"
            strokeOpacity="0.25"
          />
          <path
            d="M12 2a10 10 0 0 1 10 10"
            stroke={color}
            strokeWidth="3"
            strokeLinecap="round"
          />
        </svg>
      </Box>
    </>
  );
};
