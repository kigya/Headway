import base.configureDesktopApplication
import extension.desktopMainDependencies
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.convention.base.desktopApplication)
    alias(libs.plugins.convention.component.compose)
}

configureDesktopApplication {
    mainClass.set("dev.kigya.headway.MainKt")
    packageName.set("Headway")
    packageVersion.set("1.0.0")
    iconDir.set(file("src/desktopMain/composeResources/drawable"))
    iconBaseName.set("ic_headway")
    dockName.set("Headway")
    bundleId.set("dev.kigya.headway")
    urlSchemes.set(listOf("headway"))
    jlinkModules.set(listOf("jdk.httpserver", "jdk.unsupported"))
    desktopEnvFromSecrets(gradlePropertyName = "headwayDesktopEnv")
    formats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
}

desktopMainDependencies {
    projects {
        implementation(shared)
    }
}
