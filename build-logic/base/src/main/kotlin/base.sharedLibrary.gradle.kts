import extension.libs
import org.gradle.kotlin.dsl.getting
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("internal.config.android")
    id("internal.config.desktop")
    id("internal.config.detekt")
    id("internal.config.shared.outcome")
}

configure<KotlinMultiplatformExtension> {
    androidTarget {
        tasks.withType<KotlinJvmCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(rootProject.libs.versions.java.get()))
            }
        }
    }

    jvm("desktop") {
        tasks.withType<KotlinJvmCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(rootProject.libs.versions.java.get()))
            }
        }
    }

    kotlin {
        iosArm64()
        iosX64()
        iosSimulatorArm64()
        jvm("desktop")

        sourceSets {
            val desktopMain by getting {
                dependencies {
                    implementation(libs.kotlinx.coroutinesSwing)
                }
            }
        }
    }
}
