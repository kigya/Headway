import base.configureMicroserviceApplication
import extension.microserviceDependencies

plugins {
    alias(libs.plugins.convention.base.microserviceApplication)
    alias(libs.plugins.convention.component.serialization)
}

configureMicroserviceApplication {
    version.set("1.0.0")
    mainClass.set("AdminApplicationKt")
    applicationName.set("admin")
}

microserviceDependencies {
    projects {
        implementation(common)
        api(admin.api)
        api(database.api)
    }

    libs {
        implementation(logback)
        implementation(ktor.serverCore)
        implementation(ktor.serverNetty)
        implementation(ktor.serverResources)
        implementation(ktor.statusPages)
        implementation(ktor.negotiation)
        implementation(ktor.serialization)

        implementation(koin.ktor)

        implementation(ktor.client.core)
        implementation(ktor.client.cio)
        implementation(ktor.client.content.negotiation)
        implementation(ktor.clientResources)

        testImplementation(kotlin.test)
        testImplementation(ktor.serverTestHost)
    }
}
