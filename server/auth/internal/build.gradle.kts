plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "dev.kigya.headway.auth"
version = "1.0.0"

application {
    applicationName = "auth"

    mainClass.set("dev.kigya.headway.auth.internal.AuthApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.server.common)
    api(projects.server.auth.api)
    api(projects.server.database.api)

    implementation(libs.logback)

    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.serverResources)
    implementation(libs.ktor.statusPages)
    implementation(libs.ktor.negotiation)
    implementation(libs.ktor.serialization)

    implementation(libs.ktor.auth.jwt)
    implementation(libs.koin.ktor)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.clientResources)

    implementation(libs.google.api.client)

    testImplementation(libs.ktor.serverTestHost)
}
