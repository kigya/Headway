import extension.jvmLibraryDependencies
import base.configureJvmLibrary

plugins {
    alias(libs.plugins.convention.base.jvmLibrary)
    alias(libs.plugins.convention.component.serialization)
}

configureJvmLibrary {
    archivesName.set("common")
    version.set("1.0.0")
}

jvmLibraryDependencies {
    libs {
        implementation(ktor.serialization)
        implementation(ktor.client.core)
        implementation(ktor.serverCore)
        implementation(ktor.serverResources)
        implementation(ktor.client.cio)
        implementation(ktor.clientResources)
        implementation(ktor.negotiation)
        implementation(ktor.client.content.negotiation)
        implementation(koin.core)
        implementation(ktor.statusPages)

        testImplementation(kotlin.test)
    }
}
