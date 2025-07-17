import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.convention.base.androidApplication)
    alias(libs.plugins.convention.base.desktopApplication)

    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.composeHotReload)

    alias(libs.plugins.convention.bundle.shared.ui.screen.compose)
    alias(libs.plugins.convention.bundle.android.ui.screen.compose)
}

configureAndroidApplication {
    namespace = "dev.kigya.headway"
    versionCode = 1
    versionName = "1.0.0"
    resourceConfigurations += listOf("en", "ru")
}

configureDesktopApplication {
    mainClass = "dev.kigya.headway.MainKt"
    packageName = "dev.kigya.headway"
    packageVersion = "1.0.0"
    targetFormats += TargetFormat.Dmg
    targetFormats += TargetFormat.Msi
    targetFormats += TargetFormat.Deb
}

kotlin {
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
