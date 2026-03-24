import base.configureMicroserviceApplication
import extension.microserviceDependencies

plugins {
    alias(libs.plugins.convention.base.microserviceApplication)
    alias(libs.plugins.convention.component.serialization)
    alias(libs.plugins.kover)
}

configureMicroserviceApplication {
    version.set("1.0.0")
    mainClass.set("HomeApplicationKt")
    applicationName.set("home")
}

microserviceDependencies {
    libs {
        implementation(logback)
        implementation(ktor.serverCore)
        implementation(ktor.serverNetty)
        implementation(ktor.serverResources)
        implementation(ktor.statusPages)
        implementation(ktor.negotiation)
        implementation(ktor.serialization)
        implementation(koin.ktor)

        testImplementation(kotlin.test)
        testImplementation(ktor.serverTestHost)
    }

    projects {
        implementation(common)
        api(home.api)
    }
}
