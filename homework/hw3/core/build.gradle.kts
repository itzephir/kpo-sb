import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

group = "ru.hse.store.api"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.exposed.core)
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}