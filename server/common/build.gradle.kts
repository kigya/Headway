plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.serialization)
}

group = "dev.kigya.headway.common"
version = "1.0.0"

dependencies {
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverResources)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.clientResources)
    implementation(libs.ktor.negotiation)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.koin.core)
    implementation(libs.ktor.statusPages)
}
