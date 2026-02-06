export const getMerchantDomain = (): string => {
  let domainSource = window.location.href;

  if (window.self !== window.top) {
    const { ancestorOrigins } = window.location;
    if (ancestorOrigins?.length) {
      domainSource = ancestorOrigins[ancestorOrigins.length - 1];
    } else if (document.referrer) {
      domainSource = document.referrer;
    }
  }

  try {
    const url = new URL(domainSource);
    url.search = '';
    return url.hostname;
  } catch {
    return domainSource;
  }
};
