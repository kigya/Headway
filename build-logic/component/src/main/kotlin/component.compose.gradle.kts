import com.android.build.api.dsl.CommonExtension
import detekt.DetektConfigs
import extension.androidMainDependencies
import extension.commonMainDependencies
import extension.composePlugin
import extension.libs
import extension.configureIfExists
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposePlugin.CommonComponentsDependencies.uiToolingPreview

plugins {
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

configureIfExists(CommonExtension::class.java) {
    buildFeatures.compose = true
}

configure<DetektExtension> {
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

androidMainDependencies {
    composePlugin {
        implementation(preview)
        implementation(uiTooling)
    }
}
