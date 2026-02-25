publish:
	./gradlew clean :shared:publish :compose:publish
publish-local:
	./gradlew clean :shared:publishToMavenLocal :compose:publishToMavenLocal
js-build:
	./gradlew clean :shared:jsBrowserDevelopmentLibraryDistribution
js-build-ci:
	./gradlew :shared:jsBrowserDevelopmentLibraryDistribution --configuration-cache --parallel
build:
	./gradlew clean assemble
build-ci:
	./gradlew assemble --configuration-cache --parallel
update-yar-lock:
	./gradlew kotlinUpgradeYarnLock
lint-fix:
	./gradlew ktlintFormat
lint-check:
	./gradlew ktlintCheck
shared-test:
	./gradlew :shared:allTests :shared:koverXmlReport
compose-test:
	./gradlew :compose:testAndroidHostTest :compose:koverXmlReport
build-XCFramework:
	./gradlew :compose:assembleComposeKitReleaseXCFramework
	# syncComposeResourcesToSPM runs automatically and updates Sources/ComposeResources/composeResources/
	# Before releasing: update Package.swift binaryTarget from path: to url: + checksum:

IOS_PROJECT ?= examples/ios/ConektaExample.xcodeproj
IOS_SCHEME ?= ConektaExample
IOS_SIMULATOR ?= iPhone 17 Pro
IOS_BUNDLE_ID ?= com.conekta.example
IOS_DERIVED_DATA ?= .build/ios-derived-data

ios-example: build-XCFramework
	@echo "Opening iOS example in Xcode..."
	xed "$(IOS_PROJECT)"

ios-example-open:
	@echo "Opening iOS example in Xcode (without XCFramework build)..."
	xed "$(IOS_PROJECT)"

ios-simulator:
	@echo "Booting iOS simulator: $(IOS_SIMULATOR)"
	@xcrun simctl boot "$(IOS_SIMULATOR)" 2>/dev/null || true
	@open -a Simulator
	@xcrun simctl bootstatus "$(IOS_SIMULATOR)" -b

ios-run-no-xcframework: ios-simulator
	@echo "Building, installing and launching iOS example (without XCFramework build)..."
	@xcodebuild \
		-project "$(IOS_PROJECT)" \
		-scheme "$(IOS_SCHEME)" \
		-configuration Debug \
		-destination "platform=iOS Simulator,name=$(IOS_SIMULATOR)" \
		-derivedDataPath "$(IOS_DERIVED_DATA)" \
		build
	@xcrun simctl install "$(IOS_SIMULATOR)" "$(IOS_DERIVED_DATA)/Build/Products/Debug-iphonesimulator/ConektaExample.app"
	@xcrun simctl launch "$(IOS_SIMULATOR)" "$(IOS_BUNDLE_ID)"

ios-run: build-XCFramework ios-run-no-xcframework

ios-test:
	./gradlew :shared:iosSimulatorArm64Test
sync-ios-resources:
	./gradlew :compose:syncComposeResourcesToSPM
verify-ios-resources:
	./gradlew :compose:verifyComposeResourcesSync

# Android example — usage: make android-emulator AVD=Pixel_7_API_34
AVD ?= Pixel_7_API_34
android-emulator:
	@if $$ANDROID_HOME/platform-tools/adb devices | grep -q "emulator"; then \
		echo "Emulator already running, skipping launch."; \
	else \
		echo "Starting emulator: $(AVD)"; \
		$$ANDROID_HOME/emulator/emulator -avd $(AVD) -no-snapshot-save & \
	fi

# Waits for the device to be ready, then installs and launches the example app
android-example:
	@echo "Building and installing Android example..."
	@$$ANDROID_HOME/platform-tools/adb wait-for-device
	@$$ANDROID_HOME/platform-tools/adb shell 'while [[ -z $$(getprop sys.boot_completed) ]]; do sleep 1; done'
	cd examples/android && ./gradlew installDebug
	@$$ANDROID_HOME/platform-tools/adb shell am start -n com.conekta.example/.MainActivity

# Starts emulator and runs the example app in one step
android-emulator: android-emulator
	@sleep 5
	$(MAKE) android-example
