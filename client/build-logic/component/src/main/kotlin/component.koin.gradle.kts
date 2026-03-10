import extension.androidMainDependencies
import extension.commonMainDependencies
import extension.libs
import extension.addImplementationDependencies

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    commonMainDependencies {
        libs {
            implementation(koin.core)
            implementation(koin.compose)
            implementation(koin.composeViewModel)
        }
    }
}

pluginManager.withPlugin("com.android.kotlin.multiplatform.library") {
    androidMainDependencies {
        libs {
            implementation(koin.android)
            implementation(koin.androidxCompose)
        }
    }
}

pluginManager.withPlugin("com.android.application") {
    addImplementationDependencies(
        libs.koin.core,
        libs.koin.compose,
        libs.koin.composeViewModel,
        libs.koin.android,
        libs.koin.androidxCompose,
    )
}
