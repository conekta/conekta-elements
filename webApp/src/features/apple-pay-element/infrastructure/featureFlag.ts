import { customFetch } from './customFetch';
import { IFeatureFlag } from 'common/interface';
import { replaceTrailingSlash } from 'common/util/string';

const VITE_BASE_URL = 'https://pay.stg.conekta.io/';
const baseUrl = `${replaceTrailingSlash(VITE_BASE_URL)}api/feature-flags`;

export const getFeatureFlag = async (appId: string, flagKey: string): Promise<IFeatureFlag> => {
  try {
    const response = await customFetch<IFeatureFlag>(`${baseUrl}/${appId}/${flagKey}`, {
      method: 'GET',
    });
    return response;
  } catch (e) {
    // TODO: Implement datadogLogs.logger.error('feature_flags_error', { error: e });
    throw e;
  }
};
