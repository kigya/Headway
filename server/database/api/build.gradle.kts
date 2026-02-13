plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.serialization)
}

group = "dev.kigya.headway.database.api"
version = "1.0.0"

base {
    archivesName.set("database-api")
}

dependencies {
    implementation(projects.server.common)
    implementation(libs.ktor.serverResources)
}
