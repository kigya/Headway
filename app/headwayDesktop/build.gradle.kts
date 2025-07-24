import base.formats
import extension.desktopMainDependencies
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.convention.base.desktopApplication)
    alias(libs.plugins.convention.component.compose)
}

configureDesktopApplication {
    mainClass.set("dev.kigya.headway.MainKt")
    packageName.set("dev.kigya.headway")
    packageVersion.set("1.0.0")
    formats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
}

desktopMainDependencies {
    projects {
        implementation(shared)
    }
}
