import extension.commonMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)
    alias(libs.plugins.convention.component.serialization)
}

commonMainDependencies {
    projects {
        implementation(core.networkApi)
        implementation(core.outcome)
    }
    libs {
        implementation(coroutines.core)
        implementation(serializationJson)
    }
}
