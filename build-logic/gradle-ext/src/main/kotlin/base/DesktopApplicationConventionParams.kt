package base

import javax.inject.Inject
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

/**
 * Holds all configurable parameters for the Desktop Application convention plugin.
 *
 * Your `build.gradle.kts` can call:
 * ```
 * desktopApplication {
 *   mainClass.set("dev.kigya.headway.MainKt")
 *   packageName.set("com.example.desktop")
 *   packageVersion.set("1.2.3")
 *   formats(TargetFormat.Dmg, TargetFormat.Msi)
 * }
 * ```
 *
 * @property mainClass
 *   Fully qualified name of the desktop application’s entry point class
 *   (default = `"MainKt"`).
 *
 * @property packageName
 *   Base package to use for your native distributions (default = `"com.example.desktop"`).
 *
 * @property packageVersion
 *   Version string for your native distributions (default = `"1.0.0"`).
 *
 * @property targetFormats
 *   List of native distribution formats to build
 *   (e.g. `TargetFormat.Dmg`, `TargetFormat.Deb`).
 *   Defaults to an empty list.
 */
public abstract class DesktopApplicationConventionParams @Inject constructor(
    objects: ObjectFactory
) {
    @get:Input
    public abstract val mainClass: Property<String>

    @get:Input
    public abstract val packageName: Property<String>

    @get:Input
    public abstract val packageVersion: Property<String>

    @get:Input
    public abstract val targetFormats: ListProperty<TargetFormat>

    init {
        mainClass.convention("MainKt")
        packageName.convention("com.example.desktop")
        packageVersion.convention("1.0.0")
        targetFormats.convention(emptyList())
    }
}

/**
 * DSL helper to set one or more native distribution formats in a single call.
 *
 * Usage:
 * ```
 * desktopApplication {
 *   formats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
 * }
 * ```
 *
 * @receiver the convention parameters holder for desktop applications.
 * @param formats one or more [TargetFormat] values to apply.
 */
public fun DesktopApplicationConventionParams.formats(vararg formats: TargetFormat): Unit =
    targetFormats.set(formats.toList())
