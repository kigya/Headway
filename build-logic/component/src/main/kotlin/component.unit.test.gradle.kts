import extension.implementation
import extension.libs
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

configure<KotlinMultiplatformExtension> {
    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))

            with(libs) {
                implementation(coroutines.test)
                implementation(turbine)
                implementation(assertk)
            }
        }
    }

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant {
            sourceSetTree.set(KotlinSourceSetTree.test)

            dependencies {
                with(libs) {
                    implementation(core.ktx)
                    implementation(robolectric)
                }
            }
        }
    }
}

dependencies {
    with(libs) {
        implementation(mockk)
    }
}
