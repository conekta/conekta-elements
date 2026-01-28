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
	./gradlew :shared:allTests
