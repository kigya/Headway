import extension.addKotlinCompilerOptIns
import extension.commonMainDependencies
import extension.desktopMainDependencies
import extension.enableContextParameters
import extension.enableDataFlowBasedExhaustiveness
import extension.enableExplicitBackingFields
import extension.getInt
import extension.libs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("internal.config.desktop")
    id("internal.config.detekt")
}

configure<KotlinMultiplatformExtension> {
    android {
        val projectNameFormatted = project.path
            .drop(1)
            .replace(Regex("[-:]"), ".")
        namespace = "dev.kigya.headway.$projectNameFormatted"

        compileSdk = rootProject.libs.versions.compileSdk.getInt()
        minSdk = rootProject.libs.versions.minSdk.getInt()

        androidResources { enable = false }
        project.pluginManager.withPlugin("org.jetbrains.compose") {
            androidResources { enable = true }
        }

        lint {
            htmlReport = false
            baseline = rootProject.file("lint-baseline.xml")
        }

        packaging.resources.excludes.addAll(
            listOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
                "META-INF/DEPENDENCIES",
                "META-INF/NOTICE",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE.txt",
                "META-INF/{AL2.0,LGPL2.1}",
                "kotlin/coroutines/coroutines.kotlin_builtins",
            )
        )

        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(rootProject.libs.versions.java.get()))
        }
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(rootProject.libs.versions.java.get()))
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
        enableDataFlowBasedExhaustiveness()
        enableExplicitBackingFields()

        addKotlinCompilerOptIns(
            listOf(
                "kotlin.ExperimentalStdlibApi",
                "kotlinx.coroutines.ExperimentalCoroutinesApi",
                "org.koin.core.annotation.KoinExperimentalAPI",
                "androidx.compose.ui.ExperimentalComposeUiApi",
                "org.jetbrains.kotlin.gradle.ExperimentalWasmDsl",
                "org.jetbrains.compose.resources.ExperimentalResourceApi",
                "kotlinx.cinterop.ExperimentalForeignApi",
                "org.jetbrains.compose.resources.InternalResourceApi",
            )
        )
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
