// webApp/src/utils/cdn.ts
const CDN_BASE_URL = 'https://assets.conekta.com';

export const CDN = {
  Icons: {
    APPLE: `${CDN_BASE_URL}/checkout/img/logos/logo-apple-with-text.svg`,
    VISA: `${CDN_BASE_URL}/checkout/img/logos/logo-visa.svg`,
    MASTERCARD: `${CDN_BASE_URL}/checkout/img/logos/logo-mastercard.svg`,
  },
} as const;
