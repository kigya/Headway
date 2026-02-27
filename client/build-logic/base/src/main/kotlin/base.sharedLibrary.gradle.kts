@file:OptIn(ExperimentalWasmDsl::class)

import extension.commonMainDependencies
import extension.desktopMainDependencies
import extension.enableContextParameters
import extension.libs
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("internal.config.android")
    id("internal.config.desktop")
    id("internal.config.detekt")
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
        wasmJs {
            browser()
            binaries.executable()
        }

        enableContextParameters()
    }
}

commonMainDependencies {
    libs {
        implementation(coroutines.core)

        val outcomeLibPath = ":core:outcome"
        if (project.path != outcomeLibPath) {
            implementation(project(outcomeLibPath))
        }
    }
}

desktopMainDependencies {
    libs {
        implementation(coroutines.swing)
    }
}
