import '../styles/spinner.css';
import { COLORS } from '../constants/styles';

interface SpinnerProps {
  color?: string;
}

export const Spinner = ({ color = COLORS.WHITE }: SpinnerProps) => {
  return (
    <div className="spinner-container">
      <svg
        className="spinner-svg"
        width="24"
        height="24"
        viewBox="0 0 24 24"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
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
    </div>
  );
};
