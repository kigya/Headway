import extension.commonMainDependencies
import extension.androidMainDependencies
import extension.libs

commonMainDependencies {
    libs {
        implementation(koin.core)
        implementation(koin.compose)
    }
}

androidMainDependencies {
    libs {
        implementation(koin.android)
        implementation(koin.androidxCompose)
    }
}
