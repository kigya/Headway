package base

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.io.File

public class Prop<T>(initial: T) {
    private var v: T = initial
    public fun set(value: T) {
        v = value
    }

    public fun get(): T = v
    override fun toString(): String = v.toString()
}

public class DesktopAppConfig {
    public val mainClass: Prop<String> = Prop("MainKt")
    public val packageName: Prop<String> = Prop("App")
    public val packageVersion: Prop<String> = Prop("1.0.0")
    public val iconDir: Prop<File?> = Prop<File?>(null)
    public val iconBaseName: Prop<String> = Prop("")
    public val dockName: Prop<String> = Prop("")

    private var _formats: List<TargetFormat> = emptyList()
    public fun formats(vararg formats: TargetFormat) {
        _formats = formats.toList()
    }

    internal fun resolvedFormats(): List<TargetFormat> =
        _formats.ifEmpty {
            listOf(
                when {
                    OperatingSystem.current().isMacOsX -> TargetFormat.Dmg
                    OperatingSystem.current().isWindows -> TargetFormat.Msi
                    else -> TargetFormat.Deb
                }
            )
        }
}

public fun Project.configureDesktopApplication(block: DesktopAppConfig.() -> Unit) {
    val cfg = DesktopAppConfig().apply(block)

    pluginManager.withPlugin("org.jetbrains.compose") {
        val composeExt = extensions.getByType(ComposeExtension::class.java)
        val desktopExt = (composeExt as ExtensionAware)
            .extensions
            .getByName("desktop") as DesktopExtension

        desktopExt.application {
            mainClass = cfg.mainClass.get()
            nativeDistributions {
                packageName = cfg.packageName.get()
                packageVersion = cfg.packageVersion.get()
                targetFormats(*cfg.resolvedFormats().toTypedArray())

                val dir = cfg.iconDir.get()
                val base = cfg.iconBaseName.get()
                if (dir != null && base.isNotEmpty()) {
                    windows { iconFile.set(file("$dir/$base.ico")) }
                    macOS {
                        dockName = cfg.dockName.get()
                        iconFile.set(file("$dir/$base.icns"))
                    }
                    linux { iconFile.set(file("$dir/$base.png")) }
                }
            }
        }
    }
}
