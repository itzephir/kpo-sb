import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlinx.rpc) apply false
    alias(libs.plugins.ktor) apply false
}

subprojects {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            optIn.add("kotlin.time.ExperimentalTime")
            optIn.add("kotlin.uuid.ExperimentalUuidApi")
            freeCompilerArgs.add("-Xcontext-parameters")
        }
    }
}
