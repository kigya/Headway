import base.DesktopApplicationConventionParams
import extension.desktopMainDependencies
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.ComposePlugin

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("internal.config.desktop")
    id("internal.config.detekt")
}

val desktopExtension = project.extensions.create(
    "configureDesktopApplication",
    DesktopApplicationConventionParams::class.java,
)

project.afterEvaluate {
    configure<ComposeExtension> {
        desktop {
            application {
                mainClass = desktopExtension.mainClass.get()
                nativeDistributions {
                    targetFormats(*(desktopExtension.targetFormats.get().toTypedArray()))
                    packageName = desktopExtension.packageName.get()
                    packageVersion = desktopExtension.packageVersion.get()
                }
            }
        }
    }
}

desktopMainDependencies {
    implementation(ComposePlugin.DesktopDependencies.currentOs)
}
