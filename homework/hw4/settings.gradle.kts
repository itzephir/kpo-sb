@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "hw4"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    includeBuild("gradle/plugins")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

include(":core")
include(":apphost")
include(":orders-service")
include(":payments-service")