import base.formats
import extension.commonMainDependencies
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import kotlin.collections.listOf

plugins {
    alias(libs.plugins.convention.base.androidApplication)
    alias(libs.plugins.convention.base.desktopApplication)

    alias(libs.plugins.convention.component.compose)
}

configureAndroidApplication {
    namespace.set("dev.kigya.headway")
    versionCode.set(1)
    versionName.set("1.0.0")
    resourceConfigurations.addAll(listOf("en", "ru"))
}

configureDesktopApplication {
    mainClass.set("dev.kigya.headway.MainKt")
    packageName.set("dev.kigya.headway")
    packageVersion.set("1.0.0")
    formats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
}

kotlin {
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }
}

commonMainDependencies {
    implementation(projects.shared)
}
