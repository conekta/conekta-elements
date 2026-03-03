<p align="center">
  <img src="https://assets.conekta.com/cpanel/statics/assets/img/conekta-logo-blue-full.svg" alt="Conekta Elements" width="280"/>
</p>

<p align="center">
  <a href="https://central.sonatype.com/artifact/io.conekta/conekta-elements-compose"><img src="https://img.shields.io/maven-central/v/io.conekta/conekta-elements-compose?label=maven-central" alt="Maven Central"/></a>
  <img src="https://img.shields.io/badge/kotlin-2.1.0-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin 2.1.0"/>
  <img src="https://img.shields.io/badge/license-MIT-green" alt="License MIT"/>
</p>

# Conekta Elements

Kotlin Multiplatform payment UI library for Android, iOS, and Web. Provides card tokenization components built on top of the [Conekta API](https://developers.conekta.com).

## Table of Contents

- [Modules](#modules)
- [Installation](#installation)
  - [Android](#android)
  - [iOS](#ios)
  - [Web](#web)
- [Public Key](#public-key)
- [Usage](#usage)
  - [Android Usage](#android-usage)
  - [iOS Usage](#ios-usage)
  - [Web Usage](#web-usage)
- [Development](#development)
  - [Prerequisites](#prerequisites)
  - [Project Structure](#project-structure)
  - [Build Commands](#build-commands)
  - [Running Tests](#running-tests)
  - [Linting](#linting)
  - [Publishing](#publishing)
- [CI Pipeline](#ci-pipeline)
- [Examples](#examples)
- [License](#license)

---

## Modules

| Module | Artifact | Description | Platforms |
|---|---|---|---|
| `shared` | `io.conekta:conekta-elements-shared` | Core business logic, HTTP client, tokenizer, crypto | Android, iOS, JS |
| `compose` | `io.conekta:conekta-elements-compose` | Compose Multiplatform UI components | Android, iOS |
| `webApp` | `@conekta/elements` (npm) | React + TypeScript library | Web |

---

## Installation

### Android

#### With Compose UI (recommended)

```kotlin
dependencies {
    implementation("io.conekta:conekta-elements-compose:0.2.0-beta.2")
}
```

This includes `conekta-elements-shared` transitively. No need to add both.

#### Without Compose (core logic only)

```kotlin
dependencies {
    implementation("io.conekta:conekta-elements-shared:0.2.0-beta.2")
}
```

#### Repository setup

Artifacts are published to **Maven Central**. No additional repository configuration required.

For **GitHub Packages** (pre-release builds):

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/conekta/conekta-elements")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GP_USER")
                password = providers.gradleProperty("gpr.token").orNull ?: System.getenv("GP_TOKEN")
            }
        }
    }
}
```

#### Coil setup (required for compose module)

The compose module uses [Coil 3](https://coil-kt.github.io/coil/) to load card brand icons from Conekta's CDN. Initialize it in your `Application` class:

```kotlin
import coil3.ImageLoader
import coil3.SingletonImageLoader
import io.conekta.compose.ConektaImageLoader

class MyApp : Application(), SingletonImageLoader.Factory {
    override fun newImageLoader(context: Context): ImageLoader {
        return ConektaImageLoader.newImageLoader(context)
    }
}
```

Register in `AndroidManifest.xml`:

```xml
<application android:name=".MyApp" ... >
```

### iOS

The `compose` module produces `composeKit.xcframework`, distributed as a zip asset in every [GitHub release](https://github.com/conekta/conekta-elements/releases).

#### Option 1 — Swift Package Manager

Add the package dependency in Xcode via **File > Add Package Dependencies** using the repository URL, or add a binary target in your `Package.swift`:

```swift
let package = Package(
    ...
    targets: [
        .binaryTarget(
            name: "composeKit",
            url: "https://github.com/conekta/conekta-elements/releases/download/0.2.0-beta.2/composeKit.xcframework.zip",
            checksum: "e5fc1a7030756eea3f8cc4c229c178baac9369e4c3ba3238274f03824fdb02b2"
        ),
        .target(
            name: "YourTarget",
            dependencies: ["composeKit"]
        )
    ]
)
```

The checksum is available in `checksum.txt` included in the release assets.

#### Option 2 — Direct XCFramework

1. Download `composeKit.xcframework.zip` from the [latest release](https://github.com/conekta/conekta-elements/releases/tag/0.2.0-beta.2)
2. Unzip and drag `composeKit.xcframework` into your Xcode project
3. In your target > **General** > **Frameworks, Libraries, and Embedded Content**, set it to **Embed & Sign**

#### Requirements

- Xcode 15+
- iOS 15+

### Web

```bash
npm install @conekta/elements
```

See [`webApp/README.md`](./webApp/README.md) for full documentation.

---

## Public Key

All platforms require a **Conekta public key** to tokenize cards. Get yours from the [Conekta Dashboard](https://panel.conekta.com/):

---

## Usage

### Android Usage

```kotlin
import io.conekta.compose.tokenizer.ConektaTokenizer
import io.conekta.elements.tokenizer.models.TokenizerConfig
import io.conekta.elements.tokenizer.models.TokenizerError

@Composable
fun PaymentScreen() {
    ConektaTokenizer(
        config = TokenizerConfig(
            publicKey = "key_xxxxx",
            merchantName = "My Store",
            collectCardholderName = true,
        ),
        onSuccess = { result ->
            println("Token: ${result.token}")
            println("Last four: ${result.lastFour}")
        },
        onError = { error ->
            when (error) {
                is TokenizerError.ValidationError -> println("Validation: ${error.message}")
                is TokenizerError.NetworkError -> println("Network: ${error.message}")
                is TokenizerError.ApiError -> println("API ${error.code}: ${error.message}")
            }
        },
    )
}
```

### iOS Usage

```swift
import SwiftUI
import composeKit

struct ContentView: View {
    @State private var showingAlert = false
    @State private var alertMessage = ""

    var body: some View {
        ConektaTokenizerView(
            config: TokenizerConfig(
                publicKey: "key_xxxxx",
                merchantName: "My Store",
                collectCardholderName: true
            ),
            onSuccess: { result in
                alertMessage = "Token: \(result.token)"
                showingAlert = true
            },
            onError: { error in
                if let apiError = error as? TokenizerError.TokenizerApiError {
                    alertMessage = "\(apiError.code): \(apiError.message)"
                } else if let networkError = error as? TokenizerError.TokenizerNetworkError {
                    alertMessage = networkError.message
                } else {
                    alertMessage = "Payment could not be processed."
                }
                showingAlert = true
            }
        )
    }
}
```

### Web Usage

```tsx
import { ConektaProvider, ExpressCheckout } from '@conekta/elements';

function App() {
  return (
    <ConektaProvider publicKey="key_xxx" environment="sandbox">
      <ExpressCheckout
        amount={10000}
        currency="MXN"
        onPaymentCompleted={(result) => console.log(result)}
      />
    </ConektaProvider>
  );
}
```

---

## Development

### Prerequisites

| Tool | Version | Notes |
|---|---|---|
| JDK | 17 (`17.0.7-tem`) | Managed via `.sdkmanrc` |
| Node.js | 18 | Managed via `.nvmrc` |
| Android SDK | compileSdk 36, minSdk 24 | |
| Xcode | 15+ | iOS builds only (macOS required) |
| Kotlin | 2.1.0 | Via Gradle plugin |

Install tooling:

```bash
# Java (via SDKMAN)
sdk env install

# Node.js (via nvm)
nvm install
```

### Project Structure

```
conekta-elements/
├── shared/                 # Core KMP module (Android, iOS, JS)
│   └── src/
│       ├── commonMain/     # Shared business logic
│       │   └── kotlin/io/conekta/elements/
│       │       ├── tokenizer/
│       │       │   ├── api/           # HTTP client + API service
│       │       │   ├── crypto/        # AES + RSA encryption (expect/actual)
│       │       │   ├── formatters/    # Card number/CVV/expiry formatters
│       │       │   ├── models/        # TokenizerConfig, TokenResult, TokenizerError
│       │       │   └── validators/    # Form validation (Luhn, expiry, CVV)
│       │       └── assets/            # CDN URLs for card brand images
│       ├── androidMain/    # JCE crypto implementation
│       ├── iosMain/        # CommonCrypto/Security implementation
│       ├── jsMain/         # crypto-js + jsencrypt implementation
│       └── commonTest/     # Shared tests
├── compose/                # Compose Multiplatform UI module (Android, iOS)
│   └── src/
│       ├── commonMain/     # Shared composables
│       │   └── kotlin/io/conekta/compose/
│       │       ├── tokenizer/     # ConektaTokenizer composable
│       │       ├── components/    # TextField, CardBrandIcon, etc.
│       │       ├── theme/         # Colors, fonts
│       │       └── i18n/          # Localization (ES/EN)
│       └── androidHostTest/       # Robolectric UI tests
├── webApp/                 # React + TypeScript web library
├── examples/               # Android + iOS example apps
│   ├── android/
│   └── ios/
├── buildSrc/               # Custom Gradle tasks (string validation, resources sync)
├── Package.swift           # SPM binary target config
├── Makefile                # Common build commands
└── .github/workflows/      # CI pipeline
```

### Build Commands

All common tasks are available via `make`:

```bash
# Build
make build                  # Clean build all modules
make build-ci               # Build with configuration cache (CI)
make build-XCFramework      # Build iOS XCFramework

# Test
make shared-test            # Run shared module tests (Android, iOS, JS) + coverage
make compose-test           # Run compose UI tests (Robolectric) + coverage
make ios-test               # Run iOS simulator tests only

# Lint
make lint-check             # Check code style (ktlint)
make lint-fix               # Auto-fix code style issues

# Publish
make publish-local          # Publish to Maven Local (for local development)
make publish                # Publish to remote repositories

# JS
make js-build               # Build JS browser distribution
```

Or use Gradle directly:

```bash
# Compile specific targets
./gradlew :shared:compileKotlinIosSimulatorArm64
./gradlew :shared:compileKotlinJs
./gradlew :compose:compileKotlinIosArm64

# Run specific test suites
./gradlew :shared:testAndroidHostTest
./gradlew :shared:jsNodeTest
./gradlew :shared:iosSimulatorArm64Test
./gradlew :compose:testAndroidHostTest

# String resource validation
./gradlew :compose:validateStringsOrder
./gradlew :compose:validateStringsSpelling

# Coverage reports
./gradlew :shared:koverXmlReport
./gradlew :compose:koverXmlReport
```

### Running Tests

```bash
# All tests across all platforms
make shared-test            # shared: Android + iOS + JS + Kover coverage
make compose-test           # compose: Robolectric Android UI tests + Kover coverage

# Quick verification (compile only, no tests)
./gradlew :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinJs :compose:compileKotlinIosArm64

# Full local validation (same as CI)
make lint-check && make shared-test && make compose-test
```

Coverage reports are generated at:
- `shared/build/reports/kover/` (XML + HTML)
- `compose/build/reports/kover/` (XML + HTML)

### Linting

The project uses [ktlint](https://pinterest.github.io/ktlint/) with the following configuration (`.editorconfig`):

- Max line length: 120
- Indent: 4 spaces
- Trailing commas: allowed
- `@Composable` functions: exempt from naming rules

```bash
make lint-check   # Verify code style
make lint-fix     # Auto-fix issues
```

### Publishing

#### Maven Local (for development)

```bash
make publish-local
```

This publishes both `shared` and `compose` modules to `~/.m2/repository/`. Consumer apps can use it by adding `mavenLocal()` to their repositories.

#### Maven Central

Handled by the `deploy-maven-central.yml` workflow on release. Requires signing keys configured as repository secrets.

#### GitHub Packages

Handled by the `deploy.yml` workflow. Requires `GP_USER` and `GP_TOKEN` environment variables.

#### iOS XCFramework

```bash
make build-XCFramework
```

Produces `compose/build/XCFrameworks/release/composeKit.xcframework/` with Compose resources embedded in each slice.

---

## CI Pipeline

The CI runs on every push to `main` and on pull requests (`.github/workflows/ci.yml`):

| Job | Runner | Steps |
|---|---|---|
| `validate_ios` | macOS | iOS shared tests, verify XCFramework resources |
| `build_kmp` | Linux | Version validation, build, shared tests, compose tests, ktlint, string validation, publish dry-run |
| `build_js` | Linux | JS build, web tests + coverage, lint, npm publish dry-run, Chromatic |
| `sonarcloud` | Linux | SonarQube analysis with merged coverage from KMP + JS |

---

## Examples

Working example apps are available in the [`examples/`](./examples/) directory:

| Platform | Directory | Setup |
|---|---|---|
| Android | `examples/android/` | Set public key in `MainActivity.kt` |
| iOS | `examples/ios/` | Set public key in `Local.xcconfig` |

See [`examples/README.md`](./examples/README.md) for detailed setup instructions.

---

## License

[MIT](./LICENSE)
