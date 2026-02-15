import base.configureMicroserviceApplication
import extension.microserviceDependencies

plugins {
    alias(libs.plugins.convention.base.microserviceApplication)
    alias(libs.plugins.convention.component.serialization)
}

configureMicroserviceApplication {
    version.set("1.0.0")
    mainClass.set("GatewayApplicationKt")
    applicationName.set("gateway")
}

microserviceDependencies {
    projects {
        implementation(server.common)
        implementation(server.database.api)
        implementation(server.auth.api)
    }

    libs {
        implementation(logback)
        implementation(ktor.serverCore)
        implementation(ktor.serverNetty)
        implementation(ktor.negotiation)
        implementation(ktor.serialization)
        implementation(ktor.client.core)
        implementation(ktor.client.content.negotiation)
        implementation(ktor.client.cio)
        implementation(ktor.clientResources)
        implementation(kgraphql.ktor)
        implementation(koin.ktor)

        testImplementation(ktor.serverTestHost)
    }
}
