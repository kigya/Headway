import com.android.build.api.dsl.CommonExtension
import com.android.tools.r8.internal.ui
import detekt.DetektConfigs
import extension.configureIfExists
import extension.libs
import gradle.kotlin.dsl.accessors._29a7cb0b0c9f6580251e1795ae8fc406.commonMain
import gradle.kotlin.dsl.accessors._29a7cb0b0c9f6580251e1795ae8fc406.kotlin
import gradle.kotlin.dsl.accessors._29a7cb0b0c9f6580251e1795ae8fc406.sourceSets
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import kotlin.jvm.java
import kotlin.text.toInt

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

configure<KotlinMultiplatformExtension> {
    sourceSets {
        commonMain.dependencies {
            with(libs) {
                implementation(lifecycle.viewmodel)
                implementation(lifecycle.runtime.compose)
                implementation(compose.adaptive)
                implementation(compose.backhandler)
                implementation(kotlinx.collections.immutable)
            }
            with(compose.dependencies) {
                implementation(components.resources)
                implementation(animation)
                implementation(ui)
                implementation(material3)
            }
        }
        androidMain.dependencies {
            with(libs) {
                //detektPlugins(detekt.plugins.compose)
            }
            with(compose.dependencies) {
                implementation(preview)
                implementation(uiTooling)
            }
        }
    }
}
