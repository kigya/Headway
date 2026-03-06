import extension.androidMainDependencies
import extension.commonMainDependencies
import extension.libs

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    commonMainDependencies {
        libs {
            implementation(koin.core)
            implementation(koin.compose)
            implementation(koin.composeViewModel)
        }
    }
}

pluginManager.withPlugin("com.android.application") {
    dependencies {
        val implementationDef = "implementation"
        add(implementationDef, libs.koin.core)
        add(implementationDef, libs.koin.compose)
        add(implementationDef, libs.koin.composeViewModel)
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
    dependencies {
        val implementationDef = "implementation"
        add(implementationDef, libs.koin.android)
        add(implementationDef, libs.koin.androidxCompose)
    }
}
