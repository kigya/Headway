import com.android.build.api.dsl.CommonExtension
import detekt.DetektConfigs
import extension.compose
import extension.configureIfExists
import extension.detektPlugins
import extension.implementation
import extension.libs
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlin.jvm.java

plugins {
    id("org.jetbrains.compose")
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

configureIfExists(CommonExtension::class.java) {
    buildFeatures.compose = true
}

configure<DetektExtension> {
    config.from(rootProject.file(DetektConfigs.COMPOSE))
}
