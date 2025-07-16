import extension.libs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

configure<KotlinMultiplatformExtension> {
    sourceSets {
        commonMain.dependencies {
            with(libs) {
                implementation(bundles.coilCommon)
            }
        }
        androidMain.dependencies {
            with(libs) {
                implementation(coil.network.okhttp)
            }
        }
        iosMain.dependencies {
            with(libs) {
                implementation(ktor.client.darwin)
            }
        }
    }
}
