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

### Pre-Push Policy

- Before any `git push`, run linter checks and fix reported issues.
- Recommended command: `make lint-fix`.
- If `lint-fix` cannot be executed, run the corresponding module lint task and report the reason.

### Test Data Policy

- Keep test payloads in fixtures (`*Fixtures.kt`), not inline multiline JSON in test files.
- When adding a new payload shape, create/update a fixture helper and consume it from tests.
- Prefer deterministic fixture data unless the test explicitly validates runtime randomness.
- Tests must not contain `if`/`else` branches; use deterministic setup and explicit test cases instead.

### Color Token Policy

- Do not hardcode colors in Compose components (avoid `Color(0x...)` in feature UI files).
- For Compose UI semantic colors, use `ConektaColors` from `compose/theme/ConektaColors.kt`.
- For shared/cross-module or CDN-defined palette values, use `CDNResources.Colors` and convert with `colorFromHex(...)`.
- If a new reusable semantic color is needed for Compose, add it to `ConektaColors`.
- If a new cross-platform token is required by multiple modules, add it to `CDNResources.Colors`.

### JS NPM Dependency Version Policy

- For Kotlin/JS `npm(...)` dependencies in Gradle scripts, do not hardcode versions inline.
- Declare versions in `gradle/libs.versions.toml` under `[versions]`.
- Reference them from build scripts (e.g. `npm("jsencrypt", libs.versions.jsencrypt.get())`).

### Guard Clause Policy

- Prefer guard clauses (early returns) to reduce nesting and improve readability.
- Handle exceptional or terminal states first (`loading`, `error`, invalid input) and return early.
- Extract nested conditional blocks into focused private methods when logic grows beyond simple rendering.

## Publishing

Published to Maven Central.
