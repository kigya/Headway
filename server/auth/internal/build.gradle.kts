import base.configureMicroserviceApplication
import extension.implementation
import extension.microserviceDependencies

plugins {
    alias(libs.plugins.convention.base.microserviceApplication)
    alias(libs.plugins.convention.component.serialization)
    alias(libs.plugins.kover)
}

configureMicroserviceApplication {
    version.set("1.0.0")
    mainClass.set("AuthApplicationKt")
    applicationName.set("auth")
}

microserviceDependencies {
    libs {
        implementation(logback)
        implementation(google.api.client)

        implementation(ktor.serverCore)
        implementation(ktor.serverNetty)
        implementation(ktor.serverResources)
        implementation(ktor.statusPages)
        implementation(ktor.negotiation)
        implementation(ktor.serialization)

        implementation(ktor.auth.jwt)
        implementation(koin.ktor)

        implementation(ktor.client.core)
        implementation(ktor.client.cio)
        implementation(ktor.client.content.negotiation)
        implementation(ktor.clientResources)

        testImplementation(kotlin.test)
        testImplementation(ktor.serverTestHost)
    }

    projects {
        implementation(common)
        api(auth.api)
        api(database.api)
    }
}
