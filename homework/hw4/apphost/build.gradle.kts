plugins {
    id("ktor-service-convention")
    alias(libs.plugins.ktor)
}

group = "ru.hse.apphost"

dependencies {
    // Ktor Client for proxying requests
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
}

