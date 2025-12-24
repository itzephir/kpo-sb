plugins {
    id("ktor-service-convention")
    alias(libs.plugins.ktor)
}

group = "ru.hse.payments"

dependencies {
    implementation(projects.core)
}

