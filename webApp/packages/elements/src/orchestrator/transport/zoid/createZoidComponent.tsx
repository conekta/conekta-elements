import zoid from 'zoid';
import { iframeDefinition } from './iframeDefinition';

type CreateZoidComponentArgs = {
  tag: string;
  url: string;
};

export const createZoidComponent = ({ tag, url }: CreateZoidComponentArgs) => {
  return zoid.create({
    ...iframeDefinition,
    tag,
    url,
  });
}