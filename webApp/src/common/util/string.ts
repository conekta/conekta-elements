export const replaceTrailingSlash = (url: string) => url + (url.endsWith('/') ? '' : '/');
