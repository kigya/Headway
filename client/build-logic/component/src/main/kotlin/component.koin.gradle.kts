import extension.commonMainDependencies
import extension.androidMainDependencies
import extension.libs

commonMainDependencies {
    libs {
        implementation(koin.core)
        implementation(koin.compose)
        implementation(koin.composeViewModel)
    }
}

androidMainDependencies {
    libs {
        implementation(koin.android)
        implementation(koin.androidxCompose)
    }
}
