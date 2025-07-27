import extension.commonMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)
    alias(libs.plugins.convention.component.compose)

    alias(libs.plugins.convention.component.koin)
}

commonMainDependencies {
    projects {
        implementation(feature.splash.internal)
        implementation(navigation.internal)
    }
}
