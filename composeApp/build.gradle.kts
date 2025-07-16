import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)

    alias(libs.plugins.android.application)
    alias(libs.plugins.convention.config.android)

    alias(libs.plugins.convention.build.feature.resValues)
    alias(libs.plugins.convention.build.feature.buildConfig)
    alias(libs.plugins.convention.build.feature.compose)

    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.composeHotReload)

    alias(libs.plugins.convention.bundle.shared.ui.screen.compose)
    alias(libs.plugins.convention.bundle.android.ui.screen.compose)
}

kotlin {

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework {
            baseName = "composeApp"
            isStatic = true
        }
    }

    jvm("desktop")

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
    namespace   = "dev.kigya.headway"
    compileSdk  = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "dev.kigya.headway"
        minSdk        = libs.versions.minSdk.get().toInt()
        targetSdk     = libs.versions.targetSdk.get().toInt()
        versionCode   = 1
        versionName   = "1.0.0"
    }

    packaging {
        resources.excludes += listOf(
            "META-INF/{LICENSE,NOTICE}*.{md,txt}",
            "kotlin/coroutines/coroutines.kotlin_builtins"
        )
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose.desktop {
    application {
        mainClass = "dev.kigya.headway.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName    = "dev.kigya.headway"
            packageVersion = "1.0.0"
        }
    }
}
