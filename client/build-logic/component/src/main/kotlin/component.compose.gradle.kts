import com.android.build.api.dsl.CommonExtension
import detekt.DetektConfigs
import extension.androidMainDependencies
import extension.commonMainDependencies
import extension.configureIfExists
import extension.libs
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.compose.android.AndroidExtension

plugins {
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

configureIfExists(CommonExtension::class.java) {
    buildFeatures.compose = true
}

configureIfExists(DetektExtension::class.java) {
    config.from(rootProject.file(DetektConfigs.COMPOSE))
}

commonMainDependencies {
    libs {
        implementation(lifecycle.viewmodel)
        implementation(lifecycle.runtimeCompose)

        implementation(compose.ui)
        implementation(compose.animation)
        implementation(compose.backhandler)
        implementation(compose.componentsResources)
        implementation(compose.material3)

        implementation(compose.uiToolingPreview)
    }
}

configureIfExists(AndroidExtension::class.java) {
    androidMainDependencies {
        libs {
            implementation(compose.activity)
        }
    }
}

project.addAndroidPreviewTooling()

private fun Project.addAndroidPreviewTooling() {
    fun attach() {
        dependencies {
            add("debugImplementation", libs.compose.androidxUiTooling)
        }
    }

    pluginManager.withPlugin("com.android.library") { attach() }
    pluginManager.withPlugin("com.android.application") { attach() }
}
