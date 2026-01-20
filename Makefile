publish:
	./gradlew clean :shared:publish :compose:publish
publish-local:
	./gradlew clean :shared:publishToMavenLocal :compose:publishToMavenLocal
