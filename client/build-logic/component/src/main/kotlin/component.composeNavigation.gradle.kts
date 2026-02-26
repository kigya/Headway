import extension.commonMainDependencies
import extension.libs

commonMainDependencies {
    libs {
        implementation(compose.backhandler)
        implementation(compose.navigation3.runtime)
        implementation(compose.navigation3.ui)
        implementation(lifecycle.viewmodel.navigation3)
    }
}
