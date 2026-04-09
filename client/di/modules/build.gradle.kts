import extension.androidMainDependencies
import extension.commonMainDependencies
import extension.wasmMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)
    alias(libs.plugins.convention.component.compose)

    alias(libs.plugins.convention.component.koin)
    alias(libs.plugins.convention.component.mvi)
}

commonMainDependencies {
    projects {
        implementation(core.apollo)
        implementation(core.networkApi)
        implementation(core.storageApi)
        implementation(core.storageSecure)
        implementation(core.sessionInternal)
        implementation(di.diApi)
        implementation(feature.splashDi)
        implementation(feature.authDi)
        implementation(feature.homeDi)
        implementation(feature.learnQuestionsDi)
        implementation(navigation.navigationDi)
    }
    libs {
        implementation(apollo.runtime)
        implementation(koin.core)
    }
}

androidMainDependencies {
    libs {
        implementation(koin.android)
    }
}

wasmMainDependencies {
    libs {
        implementation(kotlinx.browser.wasm.js)
    }
}
