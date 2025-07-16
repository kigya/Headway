import detekt.DetektConfigs
import extension.compose
import extension.detektPlugins
import extension.implementation
import extension.libs
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    id("org.jetbrains.compose")
    id("build-feature.compose")
}

configure<DetektExtension> {
    config.from(rootProject.file(DetektConfigs.COMPOSE))
}

dependencies {
    with(compose) {
        implementation(ui)
        implementation(material3)
    }

    with(libs) {
        with(detekt.plugins) {
            detektPlugins(compose)
        }
    }
}
