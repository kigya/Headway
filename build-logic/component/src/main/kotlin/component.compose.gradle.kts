import com.android.build.api.dsl.CommonExtension
import com.android.tools.r8.internal.ui
import extension.configureIfExists
import extension.libs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import kotlin.jvm.java

plugins {
    id("org.jetbrains.compose")
}

configureIfExists(CommonExtension::class.java) {
    buildFeatures.compose = true
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
