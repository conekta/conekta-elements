export const awsS3Url = 'https://assets.conekta.com/cpanel/statics/assets';
export const awsS3UrlCheckout = 'https://assets.conekta.com/checkout/img';
export const favicon = 'https://assets.conekta.com/website/Home/favicon.ico';
export const imgMainLogo = `${awsS3Url}/brands/logos/conekta-logo-24px.svg`;
export const imgMainLogoFullBlue = `${awsS3Url}/img/conekta-logo-blue-full.svg`;
export const imgMainLogoFullWhite = `${awsS3Url}/img/conekta-logo-full.svg`;
export const imgPoweredLogoWhite = `${awsS3Url}/img/powered-by-conekta-light.png`;
export const imgPoweredLogoBlack = `${awsS3Url}/img/powered-by-conekta-dark.png`;
export const imgMainLogoWhite = `${awsS3Url}/img/conekta_white.svg`;
export const imgPaymentButtonLogoConekta = `${awsS3Url}/img/conekta-logo-symbol.svg`;
export const imgPaymentButtonLogoConektaWhite = `${awsS3Url}/img/conekta-logo-symbol-inverse.svg`;
export const successLogo = 'https://assets.conekta.com/checkout/img/success-card-component.svg';
export const cardLogo = `${awsS3UrlCheckout}/icons/icono-carta.png`;
export const confirmationSuccessIcon = `${awsS3UrlCheckout}/Check.webp`;
export const rejectIcon = `${awsS3UrlCheckout}/reject.webp`;

export const firstImgBackgroundAmount = `${awsS3Url}/img/bg-esfera.svg`;
export const secondImgBackgroundAmount = `${awsS3Url}/img/bg-media-esfera.svg`;
export const disabledOpenAmountImg = `${awsS3Url}/img/Ilustraciones/empty-box.svg`;

export const imgAllCards = `${awsS3Url}/brands/logos/MC%3AVisa%3AAMEX-rounded.svg`;

export const imgWhiteLabel2 = `${awsS3Url}/img/powered_by_conekta.svg`;
export const imgPoweredByMainLogo = `${awsS3Url}/img/conekta-powered-by-20px.svg`;
export const imgPadlock = `${awsS3Url}/img/https_black_24dp-%201.svg`;

export const imgError404 = `${awsS3Url}/img/link/icon_404.svg`;
export const imgTimeOut = `${awsS3Url}/img/time.svg`;
export const imgTime = `${awsS3Url}/img/link/time.svg`;
export const imgIconError = `${awsS3Url}/img/link/icon_error.svg`;
export const imgBill = `${awsS3Url}/img/link/bill.svg`;
export const imgIconCancelled = `${awsS3Url}/img/link/icon_cancelled.svg`;
export const imgCalendar = `${awsS3Url}/img/link/calendar.svg`;
export const imgShoppingCart = `${awsS3Url}/img/icons/white-shopping-cart-24px.svg`;
export const cvvIcon = `${awsS3Url}/img/icons/cvv-icon-32x32.svg`;

export const paymentBrandLogos = {
  american_express_24px: `${awsS3UrlCheckout}/logos/amex.svg`,
  american_express: `${awsS3Url}/brands/logos/amex.svg`,
  bbva: `${awsS3UrlCheckout}/logos/bbva.svg`,
  bbva_white: `${awsS3UrlCheckout}/logos/bbva-white.svg`,
  cards_logos: `${awsS3Url}/brands/logos/Sellos-bancarios-24-px.svg`,
  carnet: `${awsS3Url}/img/carnet.svg`,
  mc_24px: `${awsS3UrlCheckout}/logos/master-card.svg`,
  mc: `${awsS3Url}/brands/logos/mastercard.svg`,
  oxxo: `${awsS3Url}/brands/logos/OXXO_PAY_logo.svg`,
  oxxo_img: `${awsS3Url}/img/oxxo-pay-2019.svg`,
  spei: `${awsS3Url}/brands/logos/transfer-circular.svg`,
  spei_img: `${awsS3Url}/img/spei.svg`,
  visa_24px: `${awsS3UrlCheckout}/logos/visa.svg`,
  visa: `${awsS3Url}/brands/logos/visa.svg`,
  nelo: `${awsS3Url}/brands/logos/nelo.svg`,
  seven_eleven: `${awsS3UrlCheckout}/logos/logo-seven-eleven.svg`,
  farmacias_ahorro: `${awsS3UrlCheckout}/logos/logo-farmacia-del-ahorro.svg`,
  circlek: `${awsS3UrlCheckout}/logos/logo-circlek.svg`,
  extra: `${awsS3UrlCheckout}/logos/logo-extra.svg`,
  benavides: `${awsS3UrlCheckout}/logos/logo-benavides.png`,
  wallmart: `${awsS3Url}/brands/logos/wallmart-24x24.svg`,
  bodega_aurrera: `${awsS3Url}/brands/logos/bodegaaurrera-24x24.svg`,
  sams: `${awsS3Url}/brands/logos/sams-24x24.svg`,
  super_kiosko: `${awsS3Url}/brands/logos/superkiosko-24x24.svg`,
  waldos: `${awsS3Url}/brands/logos/waldos-24x24.svg`,
  apple: `${awsS3UrlCheckout}/logos/logo-apple-with-text.svg`,
  klarna: `${awsS3UrlCheckout}/klarna.svg`,
  creditea: `${awsS3UrlCheckout}/creditea.svg`,
  aplazo: `${awsS3UrlCheckout}/aplazo.svg`,
};

export enum PaymentMethodType {
    BankTransfer = 'BankTransfer',
    Card = 'Card',
    MultipleCards = 'MultipleCards',
    Cash = 'Cash',
    Bnpl = 'Bnpl',
    PayByBank = 'PayByBank',
    Apple = 'Apple',
    Google = 'Google',
}

export const checkoutRequestType = {
  hostedPayment: 'HostedPayment',
  integration: 'Integration',
  paymentLink: 'PaymentLink',
};

export const SHOPIFY_METADATA_KEYS = ['shopify_order'];