import org.gradle.api.artifacts.VersionCatalogsExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

group = "ru.hse"
version = "1.0-SNAPSHOT"

/* ---------------- version catalog ---------------- */

val libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")

/* ---------------- application ---------------- */

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

/* ---------------- kotlin ---------------- */

kotlin {
    jvmToolchain(17)

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        optIn.add("kotlin.time.ExperimentalTime")
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

/* ---------------- dependencies ---------------- */

dependencies {

    // Kotlinx
    implementation(libs.findLibrary("kotlinx-coroutines-core").get())
    implementation(libs.findLibrary("kotlinx-serialization-core").get())
    implementation(libs.findLibrary("kotlinx-serialization-json").get())

    // Ktor Server
    implementation(libs.findLibrary("ktor-server-core").get())
    implementation(libs.findLibrary("ktor-server-netty").get())
    implementation(libs.findLibrary("ktor-server-config-yaml").get())
    implementation(libs.findLibrary("ktor-server-call-logging").get())
    implementation(libs.findLibrary("ktor-server-content-negotiation").get())
    implementation(libs.findLibrary("ktor-serialization-kotlinx-json").get())

    // Database
    implementation(libs.findLibrary("exposed-core").get())
    implementation(libs.findLibrary("exposed-dao").get())
    implementation(libs.findLibrary("exposed-jdbc").get())
    implementation(libs.findLibrary("exposed-kotlin-datetime").get())
    implementation(libs.findLibrary("postgresql").get())
    implementation(libs.findLibrary("hikari").get())

    // Dependency Injection
    implementation(libs.findLibrary("koin-core").get())
    implementation(libs.findLibrary("koin-ktor").get())
    implementation(libs.findLibrary("koin-logger-slf4j").get())

    // Kafka
    implementation(libs.findLibrary("kafka-clients").get())

    // Logging
    implementation(libs.findLibrary("logback-classic").get())
}
