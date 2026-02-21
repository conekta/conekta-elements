import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kover)
    alias(libs.plugins.mavenPublish)
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
        }.configure {
            isIncludeAndroidResources = true
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
    val xcf = XCFramework(xcfName)
    val iosTargets = listOf(iosX64(), iosArm64(), iosSimulatorArm64())

    iosTargets.forEach {
        it.binaries.framework {
            baseName = xcfName
            binaryOption("bundleId", "$group.$xcfName")
            xcf.add(this)
            isStatic = true
            export(project(":shared"))
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
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.material.icons.extended)
                implementation(libs.compose.components.resources)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                api(project(":shared"))

                // Coil for async image loading from CDN (exported as api for consumers)
                api(libs.coil.compose)
                api(libs.coil.network.ktor)
                api(libs.coil.svg)
                api(libs.ktor.client.core)
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
                implementation(libs.compose.ui.tooling.preview)
                implementation(libs.androidx.activity.compose)
                api(libs.ktor.client.okhttp)
            }
        }

        getByName("androidHostTest") {
            dependencies {
                implementation(libs.robolectric)
                implementation(libs.core)
                implementation(libs.junit)
                implementation(libs.compose.ui.test.junit4)
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
                api(libs.ktor.client.darwin)
            }
        }
    }
}
mavenPublishing {
    publishToMavenCentral()
    if (project.findProperty("signingInMemoryKey")?.toString()?.isNotBlank() == true) {
        signAllPublications()
    }

    coordinates(
        groupId = project.property("conekta.group") as String,
        artifactId = "conekta-elements-compose",
        version = project.property("conekta.version") as String,
    )

    pom {
        name.set("Conekta Elements - Compose")
        description.set("Kotlin Multiplatform payment UI library – Compose Multiplatform UI module")
        url.set("https://github.com/conekta/conekta-elements")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("conekta")
                name.set("Conekta")
                email.set("engineering@conekta.com")
            }
        }
        scm {
            url.set("https://github.com/conekta/conekta-elements")
            connection.set("scm:git:git://github.com/conekta/conekta-elements.git")
            developerConnection.set("scm:git:ssh://git@github.com/conekta/conekta-elements.git")
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

// Embed Compose resources directly inside the XCFramework slices.
// CMP's DefaultIosResourceReader looks for composeResources/ inside Frameworks/*.framework,
// so resources placed here are found automatically at runtime — no extra steps for consumers.
tasks.register("embedComposeResourcesToXCFramework") {
    group = "build"
    description = "Copies Compose resources into each XCFramework slice so CMP can find them at runtime"

    // Use the committed Sources/ComposeResources as input — these are already the processed
    // resources (with qualifier prefix) kept in sync by syncComposeResourcesToSPM.
    val resourcesDir =
        rootProject.layout.projectDirectory.dir(
            "Sources/ComposeResources/compose-resources/composeResources",
        )
    val xcfDir = layout.buildDirectory.dir("XCFrameworks/release/composeKit.xcframework")

    inputs.files(resourcesDir.asFileTree)
    outputs.dir(xcfDir)

    doLast {
        val src = resourcesDir.asFile
        val xcf = xcfDir.get().asFile

        val archDirs = xcf.listFiles { file -> file.isDirectory && file.name.startsWith("ios-") }
        val slices =
            archDirs?.flatMap { arch ->
                val frameworks = arch.listFiles { f -> f.isDirectory && f.extension == "framework" }
                frameworks?.toList().orEmpty()
            }

        if (slices.isNullOrEmpty()) {
            logger.warn("No XCFramework slices found in ${xcf.path}")
        } else {
            slices.forEach { framework ->
                val dest = framework.resolve("composeResources")
                dest.deleteRecursively()
                src.copyRecursively(dest)
            }
        }
    }
}

tasks.matching { it.name == "assembleComposeKitReleaseXCFramework" }.configureEach {
    finalizedBy("embedComposeResourcesToXCFramework")
}

tasks.register<VerifyComposeResourcesSyncTask>("verifyComposeResourcesSync") {
    group = "verification"
    description = "Fails if the XCFramework slices are missing Compose resources"

    builtResourcesDir = layout.buildDirectory.dir("processedResources/iosArm64/main/composeResources")
    spmResourcesDir =
        layout.buildDirectory.dir(
            "XCFrameworks/release/composeKit.xcframework/ios-arm64/composeKit.framework/composeResources",
        )
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
                    "*ComposableSingletons*",
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
