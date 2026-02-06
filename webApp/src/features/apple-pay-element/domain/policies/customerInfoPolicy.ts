import { CustomerInfo } from "common/interface";

export const isCustomerInfoComplete = (customerInfo?: CustomerInfo): boolean => {
    return Boolean(customerInfo?.name && customerInfo?.email);
};

export const requiresCustomerInfo = (customerInfo?: CustomerInfo): boolean => !isCustomerInfoComplete(customerInfo);
