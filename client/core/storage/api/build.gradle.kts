import extension.commonMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)
}

commonMainDependencies {
    projects {
        implementation(core.outcome)
    }
}
