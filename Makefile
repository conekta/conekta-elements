publish:
	./gradlew clean :shared:publish :compose:publish
publish-local:
	./gradlew clean :shared:publishToMavenLocal :compose:publishToMavenLocal
js-build:
	./gradlew clean :shared:jsBrowserDevelopmentLibraryDistribution
build:
	./gradlew clean build
update-yar-lock:
	./gradlew kotlinUpgradeYarnLock
