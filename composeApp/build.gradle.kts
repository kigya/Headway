import org.gradle.kotlin.dsl.getting
import org.gradle.platform.base.internal.DefaultBinaryNamingScheme.component
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import kotlin.collections.listOf

plugins {
    with(libs.plugins.convention) {
        alias(base.androidApplication)
        alias(base.desktopApplication)

        alias(component.compose)
    }
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
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.shared)
            }
        }
    }
}
