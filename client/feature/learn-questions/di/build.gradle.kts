import extension.commonMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)
    alias(libs.plugins.convention.component.compose)
    alias(libs.plugins.convention.component.koin)
    alias(libs.plugins.convention.component.mvi)
}

commonMainDependencies {
    projects {
        implementation(core.sessionInternal)
        implementation(di.diApi)
        implementation(feature.learnQuestionsApi)
        implementation(feature.learnQuestionsInternal)
        implementation(navigation.navigationApi)
    }
}
