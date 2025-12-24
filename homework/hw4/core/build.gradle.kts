plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

group = "ru.hse.core"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(17)
    
    compilerOptions {
        optIn.add("kotlin.time.ExperimentalTime")
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

dependencies {
    // Kotlinx
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)

    // Ktor Server
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.config.yaml)

    // Database
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.postgresql)
    implementation(libs.hikari)

    // Kafka
    implementation(libs.kafka.clients)
}