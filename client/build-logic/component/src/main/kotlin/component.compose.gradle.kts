
import detekt.DetektConfigs
import extension.androidMainDependencies
import extension.commonMainDependencies
import extension.configureIfExists
import extension.invoke
import extension.libs
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}


configureIfExists(DetektExtension::class.java) {
    config.from(rootProject.file(DetektConfigs.COMPOSE))
}

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    commonMainDependencies {
        libs {
            implementation(lifecycle.viewmodel)
            implementation(lifecycle.runtimeCompose)

            implementation(compose.ui)
            implementation(compose.animation)
            implementation(compose.backhandler)
            implementation(compose.componentsResources)
            implementation(compose.material3)
            implementation(immutableCollections)

            implementation(compose.uiToolingPreview)
        }
    }
}

pluginManager.withPlugin("com.android.application") {
    dependencies {
        val implementationDef = "implementation"
        libs {
            add(implementationDef, libs.lifecycle.viewmodel)
            add(implementationDef, libs.lifecycle.runtimeCompose)
            add(implementationDef, libs.compose.ui)
            add(implementationDef, libs.compose.animation)
            add(implementationDef, libs.compose.backhandler)
            add(implementationDef, libs.compose.componentsResources)
            add(implementationDef, libs.compose.material3)
            add(implementationDef, libs.immutableCollections)
            add(implementationDef, libs.compose.uiToolingPreview)
        }
    }
}

pluginManager.withPlugin("com.android.kotlin.multiplatform.library") {
    androidMainDependencies {
        libs {
            implementation(compose.activity)
        }
    }
}

pluginManager.withPlugin("com.android.application") {
    dependencies {
        add("implementation", libs.compose.activity)
    }

    dependencies {
        add("debugImplementation", libs.compose.androidxUiTooling)
    }
}
