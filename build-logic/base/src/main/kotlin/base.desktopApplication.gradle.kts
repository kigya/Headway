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
        compose.desktop {
            application {
                mainClass = desktopExtension.mainClass.get()
                nativeDistributions {
                    packageName = desktopExtension.packageName.get()
                    packageVersion = desktopExtension.packageVersion.get()
                    targetFormats(*(desktopExtension.targetFormats.get().toTypedArray()))

                    val dir = desktopExtension.iconDir.get()
                    val baseName = desktopExtension.iconBaseName.get()

                    windows {
                        iconFile.set(project.file("$dir/$baseName.ico"))
                    }
                    macOS {
                        dockName = desktopExtension.dockName.get()
                        iconFile.set(project.file("$dir/$baseName.icns"))
                    }
                    linux {
                        iconFile.set(project.file("$dir/$baseName.png"))
                    }
                }
            }
        }
    }
}

desktopMainDependencies {
    implementation(ComposePlugin.DesktopDependencies.currentOs)
}
