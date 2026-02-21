# Conekta Elements SDK - Examples

Minimal examples showing how to render the Conekta tokenizer form on Android and iOS.

## Configuration

Both examples require a **Conekta public key** to work. Replace `YOUR_PUBLIC_KEY_HERE` in the following files:

| Platform | File                                                                 |
|----------|----------------------------------------------------------------------|
| Android  | `android/src/main/java/com/conekta/example/MainActivity.kt` (line 36) |
| iOS      | `ios/Local.xcconfig` (see iOS setup below)                            |

You can get your public key from the [Conekta Dashboard](https://panel.conekta.com/).

- **Production keys** start with `key_` (from the production environment)
- **Test/Sandbox keys** start with `key_` (from the sandbox environment at panel.stg.conekta.io)

---

## Android

### Prerequisites

- Android Studio
- JDK 17+
- Conekta Elements SDK published to Maven Local or a remote repository

### Setup

1. Add the SDK dependency in `build.gradle.kts`:

```kotlin
implementation("io.conekta:conekta-elements-compose-android:0.0.1-beta.4")
implementation("io.conekta:conekta-elements-shared-android:0.0.1-beta.4")
```

2. Register `ExampleApplication` in `AndroidManifest.xml` to enable card brand icon loading:

```xml
<application android:name=".ExampleApplication" ...>
```

3. Set your public key in `MainActivity.kt`:

```kotlin
private const val CONEKTA_PUBLIC_KEY = "YOUR_PUBLIC_KEY_HERE"
```

4. Build and run.

### Key files

| File | Description |
|------|-------------|
| `MainActivity.kt` | Renders `ConektaTokenizer` composable and handles token/error callbacks |
| `ExampleApplication.kt` | Configures Coil image loader for SVG card brand icons |
| `build.gradle.kts` | Dependencies: SDK, Compose |
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

3. Create the local config file (this file is gitignored):

```bash
cp ios/Local.xcconfig.example ios/Local.xcconfig
```

4. Edit `ios/Local.xcconfig` and set your keys:

```
DEVELOPMENT_TEAM = YOUR_TEAM_ID
CONEKTA_PUBLIC_KEY = YOUR_PUBLIC_KEY_HERE
```

5. In Xcode, set `Local.xcconfig` for both Debug and Release configurations:
   - Select the **project** (not the target) in the navigator
   - Go to the **Info** tab
   - Under **Configurations**, select `Local` from the dropdown for each configuration

6. Build and run.

### Key files

| File | Description |
|------|-------------|
| `ContentView.swift` | Renders `ConektaTokenizerView` and handles token/error callbacks |
| `ConektaTokenizerView.swift` | `UIViewControllerRepresentable` wrapper for the Compose UI |
| `ConektaExampleApp.swift` | SwiftUI app entry point |
