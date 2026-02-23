pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

rootProject.name = "ConektaExample"

includeBuild("../..") {
    dependencySubstitution {
        substitute(module("io.conekta:conekta-elements-compose")).using(project(":compose"))
        substitute(module("io.conekta:conekta-elements-shared")).using(project(":shared"))
    }
}
