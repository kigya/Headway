import detekt.DetektConfigs
import extension.addImplementationDependencies
import extension.addDebugImplementationDependencies
import extension.androidMainDependencies
import extension.commonMainDependencies
import extension.configureIfExists
import extension.libs
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project

plugins {
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

configureIfExists(DetektExtension::class.java) {
    config.from(rootProject.file(DetektConfigs.COMPOSE))
}

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    configureComposeCommonDependencies()
}

pluginManager.withPlugin("com.android.kotlin.multiplatform.library") {
    configureComposeAndroidMainDependencies()
}

pluginManager.withPlugin("com.android.application") {
    configureComposeAndroidAppDependencies()
}

private fun Project.configureComposeCommonDependencies() {
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

private fun Project.configureComposeAndroidMainDependencies() {
    androidMainDependencies {
        libs {
            implementation(compose.activity)
        }
    }
}

private fun Project.configureComposeAndroidAppDependencies() {
    addImplementationDependencies(
        libs.lifecycle.viewmodel,
        libs.lifecycle.runtimeCompose,
        libs.compose.ui,
        libs.compose.animation,
        libs.compose.backhandler,
        libs.compose.componentsResources,
        libs.compose.material3,
        libs.immutableCollections,
        libs.compose.uiToolingPreview,
        libs.compose.activity,
    )

    addDebugImplementationDependencies(
        libs.compose.androidxUiTooling,
    )
}
