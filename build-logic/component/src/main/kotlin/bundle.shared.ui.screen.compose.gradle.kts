import extension.libs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("component.compose")
    id("component.ui.test")
    id("component.unit.test")
    id("org.jetbrains.kotlin.plugin.compose")
}

configure<KotlinMultiplatformExtension> {
    sourceSets {
        commonMain.dependencies {
            with(libs) {
                implementation(kotlinx.collections.immutable)
            }
        }
    }
}
