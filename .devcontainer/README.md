# Dev Container for conekta-elements

This dev container automatically installs Java (using SDKMAN and @sdkmanrc if present) and Node.js (using NVM and .nvmrc).

## How it works
- On startup, the container runs `setup.sh` to install the required Java and Node versions.
- Java version is read from `@sdkmanrc` if available, otherwise defaults to Java 17.
- Node version is read from `.nvmrc` if available, otherwise defaults to Node 18.

## Included Extensions
- Java Pack
- TypeScript Next
- Prettier

## Usage
Just open the repository in VS Code and reopen in the container. All dependencies will be installed automatically.
