# Conekta Elements

Kotlin Multiplatform payment UI library for Android, iOS, and Web.

## Project Structure

- `shared/` - Core business logic (Android, iOS, JS targets)
- `compose/` - Compose Multiplatform UI components (Android, iOS)
- `buildSrc/` - Custom Gradle tasks
- `webApp/` - Web application

## Gradle Custom Tasks

**Always use buildSrc** for custom tasks (not inline `doLast` blocks).

Pattern:
1. Create task class in `buildSrc/src/main/kotlin/TaskName.kt`
2. Use `@InputFiles`, `@Internal` annotations for configuration cache compatibility
3. Never access `project.*` inside `@TaskAction`
4. Register in module's `build.gradle.kts`

Existing tasks:
- `./gradlew :compose:validateStringsOrder` - Strings must be alphabetical
- `./gradlew :compose:validateStringsSpelling` - Spelling validation via LanguageTool

## Code Coverage (Kover)

- Always run tests BEFORE `koverXmlReport`
- Pattern: `./gradlew :module:testTask :module:koverXmlReport`
- Reports: `build/reports/kover/report.xml`

## String Resources

Location: `compose/src/commonMain/composeResources/values/`

Requirements:
- Must be in alphabetical order
- Validated for spelling (Spanish + English)
- Both validations run with `check` task

## Testing

```bash
make shared-test    # Shared module tests + coverage
make compose-test   # Compose module tests + coverage
```

### Test Data Policy

- Keep test payloads in fixtures (`*Fixtures.kt`), not inline multiline JSON in test files.
- When adding a new payload shape, create/update a fixture helper and consume it from tests.
- Prefer deterministic fixture data unless the test explicitly validates runtime randomness.

## Publishing

Published to Maven Central.
