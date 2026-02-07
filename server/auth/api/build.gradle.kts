plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.serialization)
    application
}

group = "dev.kigya.headway.auth.api"
version = "1.0.0"

dependencies {
    implementation(projects.server.common)
    implementation(projects.server.database.api)

    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.negotiation)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.auth.jwt)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.serverResources)
    implementation(libs.ktor.statusPages)
    implementation(libs.kgraphql.ktor)
    implementation(libs.koin.ktor)
    implementation(libs.google.api.client)

    testImplementation(libs.ktor.serverTestHost)
}
