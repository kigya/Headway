import extension.androidMainDependencies
import extension.commonMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)
    alias(libs.plugins.convention.component.koin)
    alias(libs.plugins.convention.component.serialization)
}

commonMainDependencies {
    projects {
        api(core.sessionApi)
        implementation(core.apollo)
        implementation(core.networkApi)
        implementation(core.outcome)
        implementation(core.storageApi)
    }
    libs {
        implementation(apollo.runtime)
        implementation(coroutines.core)
        implementation(koin.core)
        implementation(serializationJson)
    }
}

androidMainDependencies {
    libs {
        implementation(activity.ktx)
        implementation(play.services.auth)
    }
}
