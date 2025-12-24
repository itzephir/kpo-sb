plugins {
    id("ktor-service-convention")
    alias(libs.plugins.ktor)
}

group = "ru.hse.orders"

dependencies {
    implementation(projects.core)
    implementation(projects.paymentsService)
}

