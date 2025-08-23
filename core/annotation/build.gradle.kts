import extension.commonMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)
}

commonMainDependencies {
    libs {
        api(annotation)
    }
}
