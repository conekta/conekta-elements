# Conekta Elements SDK - Examples

Minimal examples showing how to render the Conekta tokenizer form on Android and iOS.

## Configuration

Both examples require a **Conekta public key** to work. Replace `YOUR_PUBLIC_KEY_HERE` in the following files:

| Platform | File                                                                 |
|----------|----------------------------------------------------------------------|
| Android  | `android/src/main/java/com/conekta/example/MainActivity.kt` (line 36) |
| iOS      | `ios/ConektaExample/ContentView.swift` (line 12)                      |

You can get your public key from the [Conekta Dashboard](https://panel.conekta.com/).

- **Production keys** start with `key_`
- **Test/Sandbox keys** start with `key_` (from the sandbox environment)

---

## Android

### Prerequisites

- Android Studio
- JDK 17+
- Conekta Elements SDK published to Maven Local or a remote repository

### Setup

1. Add the SDK dependency in `build.gradle.kts`:

```kotlin
implementation("io.conekta.elements:compose-android:1.0.17")
implementation("io.conekta.elements:shared-android:1.0.17")
```

2. Register `ExampleApplication` in `AndroidManifest.xml` to enable card brand icon loading:

```xml
<application android:name=".ExampleApplication" ...>
```

3. Set your public key in `MainActivity.kt`:

```kotlin
private const val CONEKTA_PUBLIC_KEY = "key_your_public_key"
```

4. Build and run.

### Key files

| File | Description |
|------|-------------|
| `MainActivity.kt` | Renders `ConektaTokenizer` composable and handles token/error callbacks |
| `ExampleApplication.kt` | Configures Coil image loader for SVG card brand icons |
| `build.gradle.kts` | Dependencies: SDK, Compose, Coil, Ktor |
| `AndroidManifest.xml` | Internet permission and Application class registration |

---

## iOS

### Prerequisites

- Xcode 15+
- iOS 15+ deployment target
- XCFramework built from the `compose` module

### Setup

1. Build the XCFramework:

```bash
./gradlew :compose:assembleComposeKitReleaseXCFramework
```

2. Add the SDK as a local Swift Package in Xcode:
   - File > Add Package Dependencies
   - Select "Add Local..." and point to the `conekta-elements` root directory
   - Add the `composeKit` library to your target

3. Set your public key in `ContentView.swift`:

```swift
private static let conektaPublicKey = "key_your_public_key"
```

4. Build and run.

### Key files

| File | Description |
|------|-------------|
| `ContentView.swift` | Renders `ConektaTokenizerView` and handles token/error callbacks |
| `ConektaTokenizerView.swift` | `UIViewControllerRepresentable` wrapper for the Compose UI |
| `ConektaExampleApp.swift` | SwiftUI app entry point |
