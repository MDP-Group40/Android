pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal() // For Gradle plugin resolution
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MDP android"
include(":app")
