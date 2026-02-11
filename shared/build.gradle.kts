
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kover)
    `maven-publish`
}
group = project.property("conekta.group") as String
version = project.property("conekta.version") as String
kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    js {
        outputModuleName = "shared"
        browser()
        nodejs()
        binaries.library()
        generateTypeScriptDefinitions()
        compilerOptions {
            target = "es2015"
            freeCompilerArgs.add("-Xes-long-as-bigint")
        }
    }
    androidLibrary {
        namespace = "io.conekta.shared"
        compileSdk = 36
        minSdk = 24

        // Enable JVM host tests so commonTest can produce Kover/Jacoco coverage.
        withHostTestBuilder {
        }

        androidResources.enable = true
    }
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
    sourceSets {
        commonMain.dependencies {
            // Ktor HTTP Client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)

            // Serialization
            implementation(libs.kotlinx.serialization.json)

            // Date/Time
            implementation(libs.kotlinx.datetime)

            // Logging
            implementation(libs.kermit)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        jsMain.dependencies {
            implementation(libs.ktor.client.js)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
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

// Disable browser tests, use only Node.js for testing
tasks.named("jsBrowserTest") {
    enabled = false
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
                )
            }
        }

        total {
            xml {
                onCheck = false // XML generated explicitly via koverXmlReport task
                title = "Shared Library Coverage"
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
