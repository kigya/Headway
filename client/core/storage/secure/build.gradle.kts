import extension.androidMainDependencies
import extension.commonMainDependencies
import extension.wasmMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)
    alias(libs.plugins.convention.component.serialization)
}

commonMainDependencies {
    projects {
        implementation(core.outcome)
        implementation(core.storageApi)
    }
    libs {
        implementation(coroutines.core)
        implementation(serializationJson)
    }
}

androidMainDependencies {
    libs {
        implementation(dataStore.preferences)
    }
}

wasmMainDependencies {
    libs {
        implementation(kotlinx.browser.wasm.js)
    }
}
