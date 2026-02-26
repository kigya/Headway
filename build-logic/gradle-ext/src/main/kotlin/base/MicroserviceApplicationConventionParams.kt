package base

import org.gradle.api.Project
import org.gradle.api.plugins.JavaApplication
import org.gradle.kotlin.dsl.configure

public class MicroserviceAppConfig {
    public val namespace: Prop<String> = Prop("")
    public val version: Prop<String> = Prop("")
    public val mainClass: Prop<String> = Prop("")
    public val applicationName: Prop<String> = Prop("")

    private var _jvmArgs: List<String> = emptyList()
    public fun jvmArgs(vararg args: String) {
        _jvmArgs = args.toList()
    }

    internal fun resolvedJvmArgs(): List<String> = _jvmArgs
}

private fun Project.formattedPathForNamespace(): String {
    val segments = path.trim(':').split(':')
    val relevant = if (segments.firstOrNull() == "server") segments.drop(1) else segments
    return relevant.joinToString(".") { it.replace('-', '.') }
}

private fun Project.defaultNamespace(): String =
    "dev.kigya.headway.${formattedPathForNamespace()}"

private fun Project.defaultGroup(): String =
    "dev.kigya.headway.${formattedPathForNamespace()}"

public fun Project.configureMicroserviceApplication(block: MicroserviceAppConfig.() -> Unit) {
    val cfg = MicroserviceAppConfig().apply {
        namespace.set(defaultNamespace())
        applicationName.set(name)
    }.apply(block)

    group = defaultGroup()
    cfg.version.get().takeIf(String::isNotBlank)?.let { version = it }

    val ns = cfg.namespace.get().ifBlank { defaultNamespace() }

    val rawMain = cfg.mainClass.get().trim()
    require(rawMain.isNotEmpty()) {
        "configureMicroserviceApplication { mainClass.set(\"...\") } is required"
    }
    val mainFqcn = if (rawMain.contains('.')) rawMain else "$ns.$rawMain"

    pluginManager.withPlugin("application") {
        extensions.configure<JavaApplication> {
            mainClass.set(mainFqcn)
            applicationName = cfg.applicationName.get().ifBlank { project.name }

            val isDevelopment = project.hasProperty("development")
            val args = mutableListOf("-Dio.ktor.development=$isDevelopment")
            args += cfg.resolvedJvmArgs()
            applicationDefaultJvmArgs = args
        }
    }
}

