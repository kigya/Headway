import base.configureMicroserviceApplication
import extension.microserviceDependencies
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.convention.base.microserviceApplication)
    alias(libs.plugins.convention.component.serialization)
    alias(libs.plugins.kover)
}

configureMicroserviceApplication {
    version.set("1.0.0")
    mainClass.set("GatewayApplicationKt")
    applicationName.set("gateway")
}

microserviceDependencies {
    projects {
        implementation(common)
        implementation(database.api)
        implementation(auth.api)
        implementation(home.api)
    }

    libs {
        implementation(logback)
        implementation(ktor.serverCore)
        implementation(ktor.serverCors)
        implementation(ktor.serverNetty)
        implementation(ktor.negotiation)
        implementation(ktor.serialization)
        implementation(ktor.client.core)
        implementation(ktor.client.content.negotiation)
        implementation(ktor.client.cio)
        implementation(ktor.clientResources)
        implementation(kgraphql.ktor)
        implementation(koin.ktor)

        testImplementation(kotlin.test)
        testImplementation(kotlinx.coroutines.core)
        testImplementation(ktor.serverTestHost)
        testImplementation(ktor.client.mock.jvm)
    }
}
