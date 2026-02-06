import { customFetch } from './customFetch';
import { IMerchantApplePaySessionPayload, IMerchantApplePaySessionResponse } from 'common/interface';
import { replaceTrailingSlash } from 'common/util/string';

const VITE_BASE_URL = 'https://pay.stg.conekta.io/';
const baseUrl = `${replaceTrailingSlash(VITE_BASE_URL)}api/apple`;

export const getMerchantAppleSession = async (
  data: IMerchantApplePaySessionPayload,
): Promise<IMerchantApplePaySessionResponse> => {
  try {
    const response = await customFetch<IMerchantApplePaySessionResponse>(`${baseUrl}/pay-session`, {
      body: JSON.stringify(data),
      headers: {
        'Content-Type': 'application/json',
      },
      method: 'POST',
    });
    return response;
  } catch (e: any) {
    // TODO: Implement datadogLogs.logger.error('apple_pay_session_error', { error: e });
    throw e;
  }
};
