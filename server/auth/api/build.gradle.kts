import base.configureJvmLibrary
import extension.jvmLibraryDependencies

plugins {
    alias(libs.plugins.convention.base.jvmLibrary)
    alias(libs.plugins.convention.component.serialization)
}

configureJvmLibrary {
    archivesName.set("auth-api")
    version.set("1.0.0")
}

jvmLibraryDependencies {
    projects {
        implementation(server.common)
        api(server.database.api)
    }

    libs {
        implementation(libs.ktor.serverResources)
    }
}
