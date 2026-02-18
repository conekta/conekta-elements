# Conekta Elements

Kotlin Multiplatform payment UI library for Android, iOS, and Web. Provides card tokenization components built on top of the [Conekta API](https://developers.conekta.com).

## Modules

| Module | Artifact | Platforms |
|---|---|---|
| `shared` | Core business logic, HTTP client, tokenizer | Android, iOS, JS |
| `compose` | Compose Multiplatform UI components | Android, iOS |
| `webApp` | React + TypeScript library | Web |

---

## Android

### With Compose UI (recommended)

```kotlin
dependencies {
    implementation("io.conekta:conekta-elements-compose:0.0.1-beta.4")
}
```

Includes `conekta-elements-shared` transitively. No need to add both.

### Without Compose (core logic only)

```kotlin
dependencies {
    implementation("io.conekta:conekta-elements-shared:0.0.1-beta.4")
}
```

### Repository setup

Artifacts are published to Maven Central. No additional repository configuration required.

For GitHub Packages:

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

### Coil setup (required for compose module)

The compose module uses Coil to load card brand icons from CDN. Initialize it in your `Application` class:

```kotlin
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

### Basic usage

```kotlin
import io.conekta.compose.tokenizer.ConektaTokenizer
import io.conekta.compose.models.TokenizerConfig

@Composable
fun PaymentScreen() {
    ConektaTokenizer(
        config = TokenizerConfig(
            publicKey = "key_xxxxx",
            merchantName = "My Store",
            collectCardholderName = true
        ),
        onSuccess = { result ->
            println("Token: ${result.token}")
            println("Last four: ${result.lastFour}")
            println("Brand: ${result.cardBrand}")
        },
        onError = { error ->
            when (error) {
                is TokenizerError.ValidationError -> println("Validation: ${error.message}")
                is TokenizerError.NetworkError -> println("Network: ${error.message}")
                is TokenizerError.ApiError -> println("API ${error.code}: ${error.message}")
            }
        }
    )
}
```

---

## iOS

The `compose` module produces an XCFramework distributed as a release asset.

Download `composeKit.xcframework.zip` from the [latest release](https://github.com/conekta/conekta-elements/releases/tag/latest) and add it to your Xcode project.

For Swift Package Manager, use the checksum file included in the release assets.

---

## Web

```bash
npm install @conekta/elements
```

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

See [`webApp/README.md`](./webApp/README.md) for full documentation.

---

## Project structure

```
conekta-elements/
├── shared/       # Core logic (HTTP client, tokenizer, crypto) — Android, iOS, JS
├── compose/      # Compose Multiplatform UI components — Android, iOS
├── webApp/       # React + TypeScript web library
└── buildSrc/     # Custom Gradle tasks
```

## Requirements

- Android minSdk 24
- Java 17
- Xcode 15+

## License

[MIT](./LICENSE)
