package base

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

open class DesktopApplicationConventionParams {
    var mainClass: String = ""
    var packageName: String = ""
    var packageVersion: String = ""
    val targetFormats: MutableList<TargetFormat> = mutableListOf()
}
