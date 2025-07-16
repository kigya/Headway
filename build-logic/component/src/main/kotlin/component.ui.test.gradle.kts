import extension.debugImplementation
import extension.implementation
import extension.libs
import gradle.kotlin.dsl.accessors._a38f0d40c97c1df3fbac9066a13273a7.compose
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

configure<KotlinMultiplatformExtension> {
    sourceSets {
        commonTest.dependencies {
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.dependencies.uiTest)
        }
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant {
            dependencies {
                with(libs) {
                    implementation(compose.ui.test.junit4.android)
                    debugImplementation(compose.ui.test.manifest)
                }
            }
        }
    }
}
