import extension.commonMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)

    alias(libs.plugins.convention.component.koin)
}

commonMainDependencies {
    projects {
        implementation(navigation.navigationApi)
        implementation(navigation.navigationInternal)
    }
}
