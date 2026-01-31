# Conekta Elements

Kotlin Multiplatform payment processing library for Android, iOS, and Web. Provides UI components and APIs for tokenization and express checkout (Apple Pay, Google Pay) on the Conekta payment platform.

## Project Structure

- `shared/` — KMP core library: HTTP client (Ktor), models, API services. Compiles to iOS framework, Android AAR, and JS library.
- `compose/` — Compose Multiplatform UI components for Android and iOS. Depends on `shared`.
- `webApp/` — React + TypeScript web app. Published as `@conekta/elements-web`. Uses Vite, Mantine UI, Storybook, Zustand.

## Tech Stack

- **Kotlin 2.3.0**, Compose Multiplatform 1.10.0, Ktor 3.1.1, Kotlinx Serialization
- **Android**: AGP 8.13.0
- **Web**: React 18, TypeScript 5, Vite, Mantine 7.5+, Storybook 7.6
- **Build**: Gradle (Kotlin DSL), npm workspaces

## Build Commands

- `./gradlew build` — Build all Kotlin modules
- `./gradlew :shared:allTests` — Run shared module tests (all platforms)
- `cd webApp && npm install && npm run dev` — Run web app locally
- `cd webApp && npm test` — Run web tests (Vitest)
- `cd webApp && npm run lint` — Lint web code (ESLint + Prettier)
- `cd webApp && npm run storybook` — Launch Storybook

## Architecture Notes

- Platform-specific HTTP engines are provided via `HttpEngineFactory` expect/actual declarations (Android, iOS, JS).
- `ConektaApiService` is the main entry point for API calls in the shared module.
- `ConektaHttpClient` configures the Ktor client with JSON serialization and auth headers.
- The web app consumes the shared module's JS output via `ConektaProvider`.
