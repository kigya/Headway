import extension.libs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

configure<KotlinMultiplatformExtension> {
    sourceSets {
        commonMain.dependencies {
            with(libs) {
                implementation(bundles.ktor)
                implementation(ktor.client.content.negotiation)
                implementation(ktor.serialization)
                implementation(napier)
                implementation(ktor.client.logging)
            }
        }
        androidMain.dependencies {
            with(libs) {
                implementation(ktor.client.android)
            }
        }
        iosMain.dependencies {
            with(libs) {
                implementation(ktor.client.darwin)
            }
        }
    }
}
