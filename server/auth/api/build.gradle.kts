plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.serialization)
}

group = "dev.kigya.headway.auth.api"
version = "1.0.0"

base {
    archivesName.set("auth-api")
}

dependencies {
    implementation(projects.server.common)
    api(projects.server.database.api)

    implementation(libs.ktor.serverResources)
}
