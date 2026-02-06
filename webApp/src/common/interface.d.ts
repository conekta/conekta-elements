import { checkoutRequestType } from 'common/util/constants';
import { CheckoutStatus, PaymentMethodType } from 'common/constants';
import { IFormCustomerFieldNames } from 'app/pages/frames/components/Form/FormCustomer/interface';
import { IShippingContact } from 'app/pages/frames/components/Form/FormShipping/interface';
import { Order } from 'app/features/ThreeDS/model/Order';

export interface OrderTemplate {
  lineItems: any[];
  customerInfo?: CustomerInfo;
  currency: string;
  metadata: any[];
  shippingLines: any[];
  taxLines: any[];
  discountLines: any[];
  subtotal?: number;
}

export interface CustomerInfo {
  corporate?: boolean;
  customerFingerprint?: string | null;
  customerId?: string;
  email: string;
  name: string;
  phone: string;
}

export interface PaymentSourceDTO {
  checkoutRequestId: string;
  paymentMethod: PaymentMethodType;
  tokenId: string;
  fingerprint?: string;
  customerInfo?: IFormCustomerFieldNames;
  shippingContact?: IShippingContact;
  productType?: string; // only for bnpl this is $provider_bnpl
  PkPayment?: ApplePayPaymentToken; // only for Apple Pay
  googlePayment?: GooglePaymentMethodData; // only for Google Pay
}

export interface CreateOrderPayload extends PaymentSourceDTO {
  checkoutAntifraudResponseID?: string;
  fillPaymentFormTime: number;
  paymentKey: string;
  paymentSourceId: string;
  savePaymentSource?: boolean;
  threeDsMode?: string;
  returnUrl?: string;
  planId?: string;
  splitPayment?: boolean;
  originalOrderId?: string;
  amount?: number;
  splitPaymentStep?: number;
}

export type SubscriptionInterval = 'day' | 'week' | 'month' | 'year' | 'half_month' | 'minute';

export interface IPlan {
  amount: number;
  createdAt: number;
  currency: string;
  expiryCount: number;
  frequency: number;
  id: string;
  interval: SubscriptionInterval;
  liveMode: boolean;
  name: string;
  object: string;
  subscriptionEnd: number;
  subscriptionStart: number;
  trialEnd: number;
  trialPeriodDays: number;
  trialStart: number;
}
interface CheckoutRequest {
  id: string;
  entityId: string;
  companyId: string;
  name: string;
  amount: number;
  quantity: number;
  liveMode: boolean;
  customProviders?: {
    Bnpl: string[] | null;
  };
  status: CheckoutStatus;
  type: string;
  recurrent: boolean;
  plans: Array<Plan>;
  expiredAt: number;
  startsAt: number;
  allowedPaymentMethods: PaymentMethod[];
  isRecurrent?: boolean;
  slug: string;
  url: string;
  returnsControlOn: string;
  needsShippingContact: boolean;
  openAmount: boolean;
  orderTemplate: OrderTemplate;
  orders: any[];
  monthlyInstallmentsEnabled: boolean;
  monthlyInstallmentsOptions: any[];
  paymentKeys: any[];
  force3dsFlow: boolean;
  excludeCardNetworks: string[];
  canNotExpire: boolean;
  redirectionTime: number;
  providers: Provider[];
  femsaMigrated: boolean;
  configuration?: CheckoutConfiguration;
  token?: {
    tokenId: string;
  };
  threeDs?: ThreeDsValues;
  maxFailedRetries?: number;
  failureUrl?: string;
  successUrl?: string;
  apiOrder?: Order;
}

export enum CheckoutRequestType {
  HostedPayment = checkoutRequestType.hostedPayment,
  Integration = checkoutRequestType.integration,
  PaymentLink = checkoutRequestType.paymentLink,
}

export interface CheckoutCharge {
  apiChargeId: string;
  status: string;
  fillPaymentFormTime: number;
  id: string;
  paymentMethod: string;
  reference: string;
}

interface CheckoutConfiguration {
  id: string;
  entityId: string;
  liveMode: boolean;
  customStyle: string;
  createCustomers: boolean;
}

interface Entity {
  id: string;
  name: string;
  status: string;
  idReferenceCompany: string;
  allowedPaymentMethods: PaymentMethodType[];
  createdAt: string;
  threeDs: string;
  conektaLogo: boolean;
  msiActive: boolean;
  notificationEnabled: boolean;
  settings?: {
    splitPaymentForEmbedded?: boolean;
    splitPaymentForPaymentLink?: boolean;
    splitPaymentForRedirected?: boolean;
  };
}

interface Provider {
  id: string;
  name: string;
  paymentMethod: string;
  haveAccount?: boolean;
}
interface AppContext {
  monthlyInstallmentsOptions: IMonthlyInstallment[];
  setMonthlyInstallmentsOptions: (monthlyInstallments: IMonthlyInstallment[]) => void;
}
interface ShippingContactContext {
  showShippingForm: boolean;
  setShowShippingForm: (value: any) => void;
}

interface ShippingContactContext {
  showShippingForm: boolean;
  setShowShippingForm: (value: any) => void;
}

type Method = 'POST' | 'GET' | 'PUT' | 'DELETE';

interface TokenRequest {
  tokenForm: TokenDTO;
  originType: string;
}

interface OpenAmount {
  entityId: string;
  companyId: string;
  merchantPath: string;
  merchantName: string;
  allowedPaymentMethods: string[];
  monthlyInstallmentsEnabled: boolean;
  monthlyInstallmentsOptions: number[];
  needsShippingContact: boolean;
  enabled: boolean;
  configuration: Record<string, any>;
  customStyleJSON: Record<string, any>;
}

interface Background {
  body: string;
  header: string;
}

interface Button {
  backgroundColor: string;
  colorText: string;
  text: string;
}

interface Logo {
  size: string;
  source: string;
  typeBase64: string;
  typeImage: string;
}

interface State {
  empty: {
    borderColor: string;
  };
  valid: {
    borderColor: string;
  };
  invalid: {
    borderColor: string;
  };
}

interface Theme {
  background: Background;
  button?: Button;
  buttonType: string;
  colors: any;
  enableWhiteLabel: boolean;
  fontSize: string;
  iframe?: any;
  inputType: string;
  letters: any;
  logo: Logo;
  state: State;
}

interface ReferenceNotificationBody {
  email: string;
  checkoutRequestId: string;
  entityId?: string;
}

interface ParamsGetJWTCreateData {
  amount: string;
  id: string;
  name: string;
  customerName: string;
  liveMode: boolean;
}

interface IPersonalizationOptions {
  backgroundMode: string;
  colorText: string;
  colorLabel: string;
  inputType: string;
  colorPrimary: string;
  hideLogo: string;
  excludeCardNetworks?: string[];
}

export interface IMerchantApplePaySessionPayload {
  isTrusted: boolean;
  methodName: string;
  validationURL: string;
  domain?: string;
  source?: string;
  companyId?: string;
}

export interface IMerchantApplePaySessionResponse {
  epochTimestamp: number;
  expiresAt: number;
  merchantSessionIdentifier: string;
  nonce: string;
  merchantIdentifier: string;
  domainName: string;
  displayName: string;
  signature: string;
}

export interface IFeatureFlag {
  id: string;
  key: string;
  value: unknown;
}
