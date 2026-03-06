
import detekt.DetektConfigs
import extension.androidMainDependencies
import extension.commonMainDependencies
import extension.configureIfExists
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
        add("implementation", libs.lifecycle.viewmodel)
        add("implementation", libs.lifecycle.runtimeCompose)
        add("implementation", libs.compose.ui)
        add("implementation", libs.compose.animation)
        add("implementation", libs.compose.backhandler)
        add("implementation", libs.compose.componentsResources)
        add("implementation", libs.compose.material3)
        add("implementation", libs.immutableCollections)
        add("implementation", libs.compose.uiToolingPreview)
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
