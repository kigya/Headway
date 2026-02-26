import base.configureJvmLibrary
import extension.jvmLibraryDependencies

plugins {
    alias(libs.plugins.convention.base.jvmLibrary)
    alias(libs.plugins.convention.component.serialization)
}

configureJvmLibrary {
    archivesName.set("database-api")
    version.set("1.0.0")
}

jvmLibraryDependencies {
    projects {
        implementation(common)
    }

    libs {
        implementation(ktor.serverResources)
    }
}
