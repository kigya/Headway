import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.convention.config.android.library)

    alias(libs.plugins.convention.build.feature.compose)

    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.composeHotReload)

    alias(libs.plugins.convention.bundle.shared.ui.screen.compose)
    alias(libs.plugins.convention.bundle.android.ui.screen.compose)
}

kotlin {

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.java.get()))
        }
    }

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework {
            baseName = "composeApp"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.shared)
            }
        }
        val androidMain by getting {
            dependencies {
                // сюда подтянутся все из bundle-android-ui-screen-compose
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutinesSwing)
            }
        }
    }
}

android {
    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
}

compose.desktop {
    application {
        mainClass = "dev.kigya.headway.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "dev.kigya.headway"
            packageVersion = "1.0.0"
        }
    }
}
