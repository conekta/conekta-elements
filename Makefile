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
ios-test:
	./gradlew :shared:iosSimulatorArm64Test
sync-ios-resources:
	./gradlew :compose:syncComposeResourcesToSPM
verify-ios-resources:
	./gradlew :compose:verifyComposeResourcesSync
