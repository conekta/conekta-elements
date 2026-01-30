# CLAUDE.md

## Project Overview

**Conekta Elements** is a multiplatform payment processing SDK that provides Express Checkout (Apple Pay / Google Pay) components. It auto-detects the user's device/browser and renders the correct payment button with brand-compliant styling.

Published as `@conekta/elements-web` to GitHub Packages.

## Monorepo Structure

```
conekta-elements/
├── webApp/          # React + TypeScript web library (npm workspace)
├── shared/          # Kotlin Multiplatform SDK (common models, exported to JS)
├── compose/         # Kotlin Multiplatform Compose UI (Android/iOS)
├── .github/         # CI workflows
├── Makefile         # Gradle build shortcuts
└── gradle/          # Version catalog (libs.versions.toml)
```

Root `package.json` defines npm workspaces: `webApp/` and `shared/build/dist/js/developmentLibrary`.

## Tech Stack

| Layer         | Technology                                    |
|---------------|-----------------------------------------------|
| UI Framework  | React 18 + TypeScript 5                       |
| Components    | Mantine 7.5                                   |
| State         | Zustand 4.5                                   |
| Build         | Vite 5.4 (web), Gradle 8.14 (KMP)            |
| Test          | Vitest 1.0 + React Testing Library + happy-dom|
| Storybook     | Storybook 7.6 + Chromatic                     |
| Lint          | ESLint 8 + Prettier 3 (TS), KtLint 1.5 (Kotlin) |
| CI            | GitHub Actions (aws-runner-set)               |
| Node          | 18.x (see .nvmrc)                             |

## Common Commands

### Web (from `webApp/` or root)

```bash
npm run dev              # Dev server on :8080
npm run build            # tsc + vite build
npm run test             # Vitest watch mode
npm run test:ci          # Vitest single run
npm run test:coverage    # Coverage report (v8)
npm run lint             # ESLint (zero warnings allowed)
npm run lint:fix         # ESLint auto-fix
npm run format           # Prettier write
npm run format:check     # Prettier check
npm run storybook        # Storybook on :6006
npm run type-check       # tsc --noEmit
```

### Kotlin / Gradle (from root)

```bash
make build-ci            # Gradle build with caching
make js-build            # Build shared KMP JS library
make js-build-ci         # Parallel JS build (CI)
make shared-test         # Run all Kotlin tests
make lint-check          # KtLint check
make lint-fix            # KtLint auto-format
make publish-local       # Publish to local Maven
```

## Architecture (webApp)

Feature-based structure. Each feature is self-contained with its own components, hooks, store, and utils. Only public API is exported via `index.ts`.

```
webApp/src/
├── features/
│   └── express-checkout/       # Phase 1 (current)
│       ├── components/         # ExpressCheckout, ApplePayButton, GooglePayButton
│       ├── hooks/              # usePaymentMethod
│       ├── store/              # Zustand stores
│       ├── utils/              # detectPaymentMethod
│       ├── constants/          # Config, styles
│       ├── styles/             # CSS
│       ├── types.ts
│       └── index.ts            # Public exports only
├── lib/                        # Formatters, shared logic
├── api/                        # HTTP client
├── providers/                  # ConektaProvider
├── shared/components/          # Shared UI components
└── utils/                      # Global utilities
```

## Coding Conventions

### TypeScript

- **Strict mode** enabled (`noUnusedLocals`, `noUnusedParameters`, `noFallthroughCasesInSwitch`)
- `interface` for public APIs, `type` for internal use
- No `any` (ESLint error). Use `unknown` if needed
- Unused vars must be prefixed with `_`
- Prefer `const`, no `var`
- No `console.log` (warn level; `console.warn` and `console.error` are allowed)

### File Naming

- Components: `PascalCase.tsx`
- Hooks: `useCamelCase.ts`
- Utils: `camelCase.ts`
- Stores: `camelCaseStore.ts`
- Types: `types.ts` or colocated

### React Patterns

- Functional components only
- Extract logic into custom hooks
- Destructure props
- Use Mantine components as base UI primitives
- Zustand selectors to prevent re-renders

### Formatting (Prettier)

- Semicolons: yes
- Single quotes: yes (JSX: double quotes)
- Print width: 100
- Tab width: 2
- Trailing commas: es5
- Arrow parens: always
- LF line endings

### Kotlin (KtLint)

- 4-space indent
- Max line length: 120
- Trailing commas allowed
- Use `@JsExport` for classes exposed to JS

## Testing

- **Framework:** Vitest + `@testing-library/react`
- **DOM:** happy-dom
- **Setup file:** `webApp/vitest.setup.ts` (imports `@testing-library/jest-dom`)
- **Globals:** enabled (`describe`, `it`, `expect` available without import)
- **Test location:** `webApp/test/` (mirrors src structure)
- **Kotlin tests:** `shared/src/commonTest/` using `kotlin.test`
- **Snapshots:** `__snapshots__/` directories alongside test files

## CI Pipeline

GitHub Actions (`.github/workflows/ci.yml`) runs on push to `main` and on PRs:

**Job `build_kmp`:** Gradle build -> shared tests -> KtLint check -> publish dry run

**Job `build_js`:** Gradle JS build -> `npm ci` -> Vitest -> ESLint -> Vite build -> publish dry run -> Chromatic (Storybook visual tests)

## Registry & Publishing

- NPM registry: GitHub Packages (`https://npm.pkg.github.com`)
- Scope: `@conekta`
- Auth: `NODE_AUTH_TOKEN` env var (see `.npmrc`)
- Build output: `webApp/dist/` (`index.js` + `index.d.ts`)

## Key Domain Notes

- Amounts are in **cents** (e.g., `10000` = $100.00 MXN)
- Apple Pay buttons must follow Apple Human Interface Guidelines
- Google Pay buttons must follow Google Brand Guidelines
- Minimum button height: 40px
- CDN assets loaded from `https://assets.conekta.com`
- Express Checkout auto-detects payment method availability per device/browser
