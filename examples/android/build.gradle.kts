plugins {
    id("com.android.application") version "8.13.0"
    id("org.jetbrains.kotlin.android") version "2.3.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0"
}

import java.util.Properties

val localProperties =
    Properties().apply {
        val file = file("local.properties")
        if (file.exists()) {
            file.inputStream().use(::load)
        }
    }

val conektaPublicKey =
    localProperties.getProperty("CONEKTA_PUBLIC_KEY")
        ?: System.getenv("CONEKTA_PUBLIC_KEY")
        ?: ""

android {
    namespace = "com.conekta.example"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.conekta.example"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "CONEKTA_PUBLIC_KEY", "\"$conektaPublicKey\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // Conekta Elements SDK
    // Resolved from the local workspace via includeBuild/dependencySubstitution in settings.gradle.kts
    implementation("io.conekta:conekta-elements-compose:0.2.0-beta.2")

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.11.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.3")

}
