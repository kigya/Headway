import extension.desktopMainDependencies
import org.jetbrains.compose.ComposePlugin

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("internal.config.desktop")
    id("internal.config.detekt")
}

desktopMainDependencies {
    implementation(ComposePlugin.DesktopDependencies.currentOs)
}
