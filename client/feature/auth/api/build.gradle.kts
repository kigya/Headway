import extension.commonMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)

    alias(libs.plugins.convention.component.compose)
    alias(libs.plugins.convention.component.serialization)
    alias(libs.plugins.convention.component.composeNavigation)
}

commonMainDependencies {
    projects {
        implementation(navigation.navigationApi)
    }
}
