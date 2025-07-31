import extension.commonMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)
    alias(libs.plugins.convention.component.compose)

    alias(libs.plugins.convention.component.koin)
    alias(libs.plugins.convention.component.composeNavigation)
}

kotlin {
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }
}

commonMainDependencies {
    projects {
        implementation(di.api)
        implementation(navigation.api)
        implementation(core.designSystem)
        implementation(feature.splash.api)
    }
}
