#!/bin/bash
set -e


# Install Java using SDKMAN
source /usr/local/sdkman/bin/sdkman-init.sh
if [ -f /workspaces/conekta-elements/.sdkmanrc ]; then
    sdk env install
fi

# Install Node using NVM
source /usr/local/nvm/nvm.sh
if [ -f /workspaces/conekta-elements/.nvmrc ]; then
    nvm install $(cat /workspaces/conekta-elements/.nvmrc)
    nvm use $(cat /workspaces/conekta-elements/.nvmrc)
    nvm alias default $(cat /workspaces/conekta-elements/.nvmrc)
fi

exec "$@"
