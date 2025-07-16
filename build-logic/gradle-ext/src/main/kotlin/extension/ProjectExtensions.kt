package extension

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.compose.ComposePlugin

inline fun <T : Any> Project.configureIfExists(type: Class<T>, config: T.() -> Unit) {
    extensions.findByType(type)?.apply(config) ?: run {
        println("Type not found $type")
    }
}

inline val Project.libs: LibrariesForLibs
    // TODO must use root project: extension libs does not exist https://github.com/gradle/gradle/issues/18237
    get() = (this as ExtensionAware).extensions.getByName("libs") as LibrariesForLibs

inline val DependencyHandler.compose: ComposePlugin.Dependencies
    get() = (this as ExtensionAware).extensions.getByName("compose") as ComposePlugin.Dependencies
