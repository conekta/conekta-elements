export type FeatureFlagPort = {
    isEnabled(appId: string, flagName: string): Promise<boolean>;
};
