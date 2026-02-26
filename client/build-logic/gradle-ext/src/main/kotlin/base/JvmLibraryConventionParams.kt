package base

import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.kotlin.dsl.configure

public class JvmLibraryConfig {
    public val archivesName: Prop<String> = Prop("")
    public val version: Prop<String> = Prop("1.0.0")
}

private fun Project.formattedPathForNamespace(): String {
    val segments = path.trim(':').split(':')
    val relevant = if (segments.firstOrNull() == "server") segments.drop(1) else segments
    return relevant.joinToString(".") { it.replace('-', '.') }
}

private fun Project.defaultNamespace(): String =
    "dev.kigya.headway.${formattedPathForNamespace()}"

private fun Project.defaultArchivesName(): String {
    val segments = path.trim(':').split(':')
    val relevant = if (segments.firstOrNull() == "server") segments.drop(1) else segments
    return relevant.joinToString("-")
}

public fun Project.configureJvmLibrary(block: JvmLibraryConfig.() -> Unit) {
    val cfg = JvmLibraryConfig().apply(block)

    group = defaultNamespace()
    version = cfg.version.get()

    val archive = cfg.archivesName.get().trim().ifEmpty(::defaultArchivesName)

    extensions.findByType(BasePluginExtension::class.java)
        ?.archivesName
        ?.set(archive)

    pluginManager.withPlugin("base") {
        extensions.configure<BasePluginExtension> {
            archivesName.set(archive)
        }
    }
}
