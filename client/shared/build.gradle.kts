import extension.commonMainDependencies
import extension.iosMainDependencies

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
        implementation(di.diModules)
        implementation(navigation.navigationApi)
        implementation(core.designSystem)
        implementation(feature.splashApi)
        implementation(feature.authApi)
        implementation(feature.homeApi)
        implementation(feature.learnQuestionsApi)
    }
}

iosMainDependencies {
    projects {
        implementation(core.sessionInternal)
    }
}
