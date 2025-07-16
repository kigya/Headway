import extension.libs
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("internal.config.android")
    id("internal.config.shared.detekt")
    id("internal.config.shared.outcome")
}

configure<KotlinMultiplatformExtension> {
    androidTarget {
        tasks.withType<KotlinJvmCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(libs.versions.java.get()))
            }
        }
    }

    jvm("desktop") { // This defines a JVM target named "desktop"
        tasks.withType<KotlinJvmCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(libs.versions.java.get()))
            }
        }
    }

    arrayOf(
        iosArm64(),
        iosX64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }
}

tasks.register("testClasses")
