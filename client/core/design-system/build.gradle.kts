import extension.androidMainDependencies
import extension.commonMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)

    alias(libs.plugins.convention.component.compose)
    alias(libs.plugins.convention.component.serialization)
}

commonMainDependencies {
    libs {
        implementation(compottie)
        implementation(compottie.dot)
    }
    projects {
        implementation(navigation.api)
    }
}

androidMainDependencies {
    libs {
        implementation(compose.activity)
    }
}
