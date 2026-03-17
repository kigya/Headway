import extension.androidMainDependencies
import extension.commonMainDependencies
import extension.iosMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)

    alias(libs.plugins.convention.component.compose)
    alias(libs.plugins.convention.component.serialization)
}

compose.resources {
    publicResClass = true
}

commonMainDependencies {
    libs {
        implementation(bundles.connectivity)
        implementation(bundles.compottie)
    }
    projects {
        implementation(navigation.api)
    }
}

iosMainDependencies {
    libs {
        implementation(libs.connectivity.compose.device)
    }
}

androidMainDependencies {
    libs {
        implementation(libs.connectivity.compose.device)
    }
}
