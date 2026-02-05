import java.net.URLClassLoader

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    `maven-publish`
}

val languageTool by configurations.creating

dependencies {
    languageTool(libs.languagetool.core)
    languageTool(libs.languagetool.es)
    languageTool(libs.languagetool.en)
}

group = project.property("conekta.group") as String
version = project.property("conekta.version") as String
compose.resources {
    publicResClass = true
    generateResClass = always
}
kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "io.conekta.compose"
        compileSdk = 36
        minSdk = 24

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        androidResources.enable = true
    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    val xcfName = "composeKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.preview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                api(project(":shared"))

                // Coil for async image loading from CDN
                implementation(libs.coil.compose)
                implementation(libs.coil.network.ktor)
                implementation(libs.ktor.client.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                // Add Android-specific dependencies here. Note that this source set depends on
                // commonMain by default and will correctly pull the Android artifacts of any KMP
                // dependencies declared in commonMain.
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.ktor.client.okhttp)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.runner)
                implementation(libs.core)
                implementation(libs.junit)
            }
        }

        iosMain {
            dependencies {
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP's default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/conekta/conekta-elements")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GP_USER")
                password = project.findProperty("gpr.token") as String? ?: System.getenv("GP_TOKEN")
            }
        }
    }

    publications {
    }
}

tasks.register("validateStringsOrder") {
    group = "verification"
    description = "Validates that all string resources are in alphabetical order"

    val projectDirPath = layout.projectDirectory.asFile
    val resourcesDir = projectDirPath.resolve("src/commonMain/composeResources")

    doLast {
        val stringsFiles = fileTree(resourcesDir) {
            include("**/strings.xml")
        }

        var hasErrors = false
        val errorMessages = mutableListOf<String>()

        stringsFiles.forEach { file ->
            val relativePath = file.relativeTo(projectDirPath)
            println("Validating: $relativePath")

            val stringNames = mutableListOf<String>()
            val lines = file.readLines()

            lines.forEach { line ->
                // Extract string name attributes
                val stringMatch = Regex("""<string\s+name="([^"]+)"""").find(line)
                if (stringMatch != null) {
                    stringNames.add(stringMatch.groupValues[1])
                }
            }

            // Check if sorted
            val sortedNames = stringNames.sorted()
            if (stringNames != sortedNames) {
                hasErrors = true
                errorMessages.add("\n❌ File: $relativePath")
                errorMessages.add("   Strings are NOT in alphabetical order!")
                errorMessages.add("   Expected order:")

                // Find and report misplaced strings
                stringNames.forEachIndexed { index, name ->
                    if (index < sortedNames.size && name != sortedNames[index]) {
                        errorMessages.add("   Position ${index + 1}: Found '$name', expected '${sortedNames[index]}'")
                    }
                }
            } else {
                println("✓ Strings in $relativePath are correctly sorted")
            }
        }

        if (hasErrors) {
            errorMessages.forEach { println(it) }
            throw GradleException("\n⚠️  String validation failed! Please sort the strings alphabetically.")
        } else {
            println("\n✓ All string resources are in alphabetical order!")
        }
    }
}

tasks.register("validateStringsSpelling") {
    group = "verification"
    description = "Validates spelling and grammar in all string resources"

    val projectDirPath = layout.projectDirectory.asFile
    val resourcesDir = projectDirPath.resolve("src/commonMain/composeResources")

    // Add languageTool configuration to the task classpath
    val languageToolClasspath = configurations.getByName("languageTool")

    doLast {
        // Create a URLClassLoader with the languageTool dependencies
        val urls = languageToolClasspath.files.map { it.toURI().toURL() }.toTypedArray()
        val classLoader = URLClassLoader(urls, this::class.java.classLoader)

        val stringsFiles = fileTree(resourcesDir) {
            include("**/strings.xml")
        }

        val languageToolClass = classLoader.loadClass("org.languagetool.JLanguageTool")
        val languagesClass = classLoader.loadClass("org.languagetool.Languages")

        var hasErrors = false
        val errorMessages = mutableListOf<String>()

        stringsFiles.forEach { file ->
            val relativePath = file.relativeTo(projectDirPath)
            val locale = when {
                file.path.contains("values-en") -> "en-US"
                else -> "es" // Default to Spanish
            }

            println("Validating spelling in: $relativePath (locale: $locale)")

            // Get language instance
            val getLanguageForShortCodeMethod = languagesClass.getMethod("getLanguageForShortCode", String::class.java)
            val language = getLanguageForShortCodeMethod.invoke(null, locale)

            // Create JLanguageTool instance
            val languageClass = classLoader.loadClass("org.languagetool.Language")
            val constructor = languageToolClass.getConstructor(languageClass)
            val langTool = constructor.newInstance(language)

            // Parse XML and check each string
            val lines = file.readLines()
            lines.forEachIndexed { lineIndex, line ->
                val stringMatch = Regex("""<string\s+name="([^"]+)"[^>]*>([^<]+)</string>""").find(line)
                if (stringMatch != null) {
                    val stringName = stringMatch.groupValues[1]
                    val text = stringMatch.groupValues[2]
                        .replace("&amp;", "&")
                        .replace("&lt;", "<")
                        .replace("&gt;", ">")
                        .replace("&quot;", "\"")
                        .replace("%s", "PLACEHOLDER")
                        .replace("%d", "NUMBER")

                    // Skip validation for certain strings
                    if (text.matches(Regex("^[A-Z/]+$")) || // All caps like "MM/YY" or "CVV"
                        text.contains("Conekta") || // Brand names
                        text.contains("Mastercard") ||
                        text.contains("American Express") ||
                        text.contains("Visa")) {
                        return@forEachIndexed
                    }

                    // Check spelling
                    val checkMethod = languageToolClass.getMethod("check", String::class.java)
                    val matches = checkMethod.invoke(langTool, text) as List<*>

                    if (matches.isNotEmpty()) {
                        matches.forEach { match ->
                            val getMessage = match!!::class.java.getMethod("getMessage")
                            val getShortMessage = match::class.java.getMethod("getShortMessage")
                            val getSuggestedReplacements = match::class.java.getMethod("getSuggestedReplacements")

                            val message = getMessage.invoke(match) as String
                            val shortMessage = try {
                                getShortMessage.invoke(match) as String
                            } catch (e: Exception) {
                                ""
                            }
                            val suggestions = getSuggestedReplacements.invoke(match) as List<*>

                            hasErrors = true
                            errorMessages.add("\n⚠️  File: $relativePath:${lineIndex + 1}")
                            errorMessages.add("   String: $stringName")
                            errorMessages.add("   Text: \"$text\"")
                            errorMessages.add("   Issue: ${shortMessage.ifEmpty { message }}")
                            if (suggestions.isNotEmpty()) {
                                errorMessages.add("   Suggestions: ${suggestions.take(3).joinToString(", ")}")
                            }
                        }
                    }
                }
            }
        }

        if (hasErrors) {
            errorMessages.forEach { println(it) }
            throw GradleException("\n⚠️  Spelling/grammar validation failed! Please fix the issues above.")
        } else {
            println("\n✓ All string resources have correct spelling and grammar!")
        }
    }
}

// Run validation automatically with check task
tasks.named("check") {
    dependsOn("validateStringsOrder")
    dependsOn("validateStringsSpelling")
}
