# iOS Example (Local Development)

This example is configured to use a local Swift package:

- `examples/ios/LocalConektaElements/Package.swift`

That local package points to:

- `compose/build/XCFrameworks/release/composeKit.xcframework`

## 1) Build the framework for local iOS testing

From repository root:

```bash
./gradlew :compose:assembleDebugIosSimulatorFatFrameworkForComposeKitXCFramework --no-daemon
```

If you need a full release XCFramework:

```bash
./gradlew :compose:assembleComposeKitReleaseXCFramework --no-daemon
```

## 2) Configure local keys

Create:

- `examples/ios/Local.xcconfig`

Based on:

- `examples/ios/Local.xcconfig.example`

## 3) Open and resolve packages in Xcode

1. Open `examples/ios/ConektaExample.xcodeproj`
2. `File > Packages > Reset Package Caches`
3. `File > Packages > Resolve Package Versions`
4. Run the app on simulator

## Notes

- Root `Package.swift` remains for distribution (GitHub URL + checksum).
- The iOS example uses the local package only, so you can iterate without publishing.
