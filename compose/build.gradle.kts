plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kover)
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

tasks.register<ValidateStringsOrderTask>("validateStringsOrder") {
    group = "verification"
    description = "Validates that all string resources are in alphabetical order"

    val resourcesDir = project.projectDir.resolve("src/commonMain/composeResources")

    projectDirPath = project.projectDir
    stringsFiles =
        project.fileTree(resourcesDir) {
            include("**/strings.xml")
        }
}

tasks.register<ValidateStringsSpellingTask>("validateStringsSpelling") {
    group = "verification"
    description = "Validates spelling and grammar in all string resources"

    val resourcesDir = project.projectDir.resolve("src/commonMain/composeResources")

    projectDirPath = project.projectDir
    stringsFiles =
        project.fileTree(resourcesDir) {
            include("**/strings.xml")
        }

    languageToolClasspath = configurations.getByName("languageTool")
}

// Run validation automatically with check task
tasks.named("check") {
    dependsOn("validateStringsOrder")
    dependsOn("validateStringsSpelling")
}

// Kover configuration for code coverage
// Reports will be generated at build/reports/kover/
// XML reports are generated automatically with check task for SonarQube integration
kover {
    reports {
        filters {
            excludes {
                classes(
                    "*.*Test*",
                    "*.test.*",
                    "*.*Mock*",
                    "*.composeResources.*",
                    "*ComposableSingletons*"
                )
            }
        }

        total {
            xml {
                onCheck = false // XML generated explicitly via koverXmlReport task
                title = "Compose UI Coverage"
            }

            html {
                onCheck = false // HTML reports on-demand only
            }
        }

        verify {
            rule {
                minBound(0) // No minimum coverage requirement for now
            }
        }
    }
}
