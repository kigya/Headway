import extension.androidMainDependencies
import extension.commonMainDependencies
import extension.iosMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)

    alias(libs.plugins.convention.component.compose)
    alias(libs.plugins.convention.component.serialization)
}

commonMainDependencies {
    libs {
        implementation(bundles.connectivity)
        implementation(bundles.compottie)
        implementation(libs.coil.compose)
        implementation(libs.coil.composeCore)
        implementation(libs.coil.networkKtor)
    }
    projects {
        implementation(navigation.navigationApi)
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
