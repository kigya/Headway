import extension.commonMainDependencies
import extension.libs

commonMainDependencies {
    libs {
        implementation(mviKotlin.core)
        implementation(mviKotlin.main)
        implementation(mviKotlin.logging)
        implementation(mviKotlin.timetravel)
        implementation(mviKotlin.extensionsCoroutines)
    }
}
