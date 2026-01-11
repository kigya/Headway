plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.serialization)
    application
}

group = "dev.kigya.headway"
version = "1.0.0"

dependencies {
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.serverCore)
}
