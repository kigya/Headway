import extension.libs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

configure<KotlinMultiplatformExtension> {
    sourceSets {
        with(libs.koin) {
            commonMain.dependencies {
                implementation(core)
                implementation(compose)
            }
            androidMain.dependencies {
                implementation(android)
                implementation(androidx.compose)
            }
        }
    }
}
