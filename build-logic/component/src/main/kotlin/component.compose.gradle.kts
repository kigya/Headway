import com.android.build.api.dsl.CommonExtension
import detekt.DetektConfigs
import extension.androidMainDependencies
import extension.commonMainDependencies
import extension.composePlugin
import extension.configureIfExists
import extension.libs
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.compose.ComposePlugin.CommonComponentsDependencies.uiToolingPreview
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
        implementation(compose.adaptive)
        implementation(compose.backhandler)
        implementation(immutableCollections)
    }
    composePlugin {
        implementation(components.resources)
        implementation(animation)
        implementation(ui)
        implementation(material3)
        implementation(uiToolingPreview)
    }
}

configureIfExists(AndroidExtension::class.java) {
    androidMainDependencies {
        composePlugin {
            implementation(preview)
            implementation(uiTooling)
        }
    }
}
