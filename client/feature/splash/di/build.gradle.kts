import extension.commonMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)

    alias(libs.plugins.convention.component.composeNavigation)
    alias(libs.plugins.convention.component.koin)
    alias(libs.plugins.convention.component.mvi)
}

commonMainDependencies {
    projects {
        implementation(feature.splash.api)
        implementation(feature.splash.internal)
        implementation(navigation.api)
    }
}
