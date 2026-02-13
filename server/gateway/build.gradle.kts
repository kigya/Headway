plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.serialization)
    application
}

group = "dev.kigya.headway.gateway"
version = "1.0.0"

application {
    mainClass.set("dev.kigya.headway.gateway.GatewayApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.server.common)
    implementation(projects.server.database.api)
    implementation(projects.server.auth.api)

    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.negotiation)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.clientResources)
    implementation(libs.kgraphql.ktor)
    implementation(libs.koin.ktor)

    testImplementation(libs.ktor.serverTestHost)
}
