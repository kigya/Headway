import extension.commonMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)
    alias(libs.plugins.convention.component.compose)

    alias(libs.plugins.convention.component.koin)
    alias(libs.plugins.convention.component.mvi)
}

commonMainDependencies {
    projects {
        implementation(di.api)
        implementation(feature.splash.di)
        implementation(feature.auth.di)
        implementation(navigation.di)
    }
}
