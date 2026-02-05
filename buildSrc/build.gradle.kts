plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.languagetool:languagetool-core:6.5")
    implementation("org.languagetool:language-es:6.5")
    implementation("org.languagetool:language-en:6.5")
}
