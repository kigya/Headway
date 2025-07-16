import extension.libs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("org.jetbrains.compose")
    id("build-feature.compose")
}

configure<KotlinMultiplatformExtension> {
    sourceSets {
        commonMain.dependencies {
            with(libs) {
                implementation(lifecycle.viewmodel)
                implementation(lifecycle.runtime.compose)
                implementation(compose.adaptive)
                implementation(compose.backhandler)
            }
            with(compose.dependencies) {
                implementation(components.resources)
                implementation(animation)
                implementation(ui)
                implementation(material3)
            }
        }
        androidMain.dependencies {
            with(compose.dependencies) {
                implementation(preview)
                implementation(uiTooling)
            }
        }
    }
}
