import extension.commonMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)
    alias(libs.plugins.convention.component.compose)

    alias(libs.plugins.convention.component.serialization)
    alias(libs.plugins.convention.component.composeNavigation)
    alias(libs.plugins.convention.component.koin)
}

commonMainDependencies {
    projects {
        implementation(navigation.api)
    }
}
