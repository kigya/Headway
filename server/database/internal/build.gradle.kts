import base.configureMicroserviceApplication
import extension.microserviceDependencies

plugins {
    alias(libs.plugins.convention.base.microserviceApplication)
    alias(libs.plugins.convention.component.serialization)
    alias(libs.plugins.ktor)
}

configureMicroserviceApplication {
    version.set("1.0.0")
    mainClass.set("DatabaseApplicationKt")
    applicationName.set("database")
}

microserviceDependencies {
    projects {
        implementation(common)
        api(database.api)
    }

    libs {
        implementation(logback)
        implementation(ktor.serverCore)
        implementation(ktor.serverNetty)
        implementation(ktor.negotiation)
        implementation(ktor.serialization)
        implementation(ktor.statusPages)
        implementation(koin.ktor)
        implementation(ktor.serverResources)

        implementation(exposed.core)
        implementation(exposed.jdbc)
        implementation(exposed.date.time)
        implementation(postgresql)
        implementation(hikaricp)

        testImplementation(ktor.serverTestHost)
    }
}
