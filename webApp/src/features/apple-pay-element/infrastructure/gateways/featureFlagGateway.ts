import { getFeatureFlag } from '../featureFlag';
import { FeatureFlagPort } from '../../application/ports/FeatureFlagPort';

export const featureFlagGateway: FeatureFlagPort = {
    async isEnabled(appId, flagName) {
        const flag = await getFeatureFlag(appId, flagName);
        return Boolean(flag?.value);
    },
};
