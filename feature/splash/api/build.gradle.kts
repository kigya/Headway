import extension.commonMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)

    alias(libs.plugins.convention.component.compose)
    alias(libs.plugins.convention.component.serialization)
}

commonMainDependencies {
    projects {
        implementation(navigation.api)
    }
}
