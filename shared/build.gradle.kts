
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
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
        binaries.library()
        generateTypeScriptDefinitions()
        compilerOptions {
            target = "es2015"
        }
    }
    androidLibrary {
        namespace = "io.conekta.shared"
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
            // put your Multiplatform dependencies here
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
