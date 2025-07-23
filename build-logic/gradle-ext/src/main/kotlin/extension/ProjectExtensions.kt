package extension

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

/**
 * Allows configuring an extension of the given [type] if it exists on the project.
 * Prints a message if the extension is not found.
 *
 * Usage:
 * ```
 * project.configureIfExists(SomeExtension::class.java) {
 *   // configure extension
 * }
 * ```
 */
public inline fun <T : Any> Project.configureIfExists(
    type: Class<T>,
    config: T.() -> Unit,
) {
    extensions.findByType(type)?.apply(config) ?: run {
        println("Type not found $type")
    }
}

/**
 * A type-safe accessor for the version catalog "libs" on the root project.
 */
public inline val Project.libs: LibrariesForLibs
    get() = (this as ExtensionAware)
        .extensions
        .getByName("libs") as LibrariesForLibs

/**
 * Shortcut to retrieve the Kotlin Multiplatform extension from the project.
 */
private val Project.kmp: KotlinMultiplatformExtension
    get() = extensions.getByType(KotlinMultiplatformExtension::class.java)

/**
 * Adds dependencies to the `commonMain` source set.
 *
 * Usage:
 * ```
 * commonMainDependencies {
 *   implementation(libs.some.library)
 * }
 * ```
 */
public fun Project.commonMainDependencies(
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kmp.sourceSets
        .named("commonMain")
        .configure {
            dependencies(configure)
        }
}

/**
 * Adds dependencies to the `androidMain` source set.
 */
public fun Project.androidMainDependencies(
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kmp.sourceSets
        .named("androidMain")
        .configure {
            dependencies(configure)
        }
}

/**
 * Adds dependencies to the `desktopMain` source set.
 */
public fun Project.desktopMainDependencies(
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kmp.sourceSets
        .named("desktopMain")
        .configure {
            dependencies(configure)
        }
}

/**
 * Adds dependencies to the `iosMain` source set.
 */
public fun Project.iosMainDependencies(
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kmp.sourceSets
        .named("iosMain")
        .configure {
            dependencies(configure)
        }
}

/**
 * Adds dependencies to the `commonTest` source set.
 */
public fun Project.commonTestDependencies(
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kmp.sourceSets
        .named("commonTest")
        .configure {
            dependencies(configure)
        }
}

/**
 * Adds dependencies to the `androidTest` source set.
 */
public fun Project.androidTestDependencies(
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kmp.sourceSets
        .named("androidTest")
        .configure {
            dependencies(configure)
        }
}

/**
 * Adds dependencies to the `desktopTest` source set.
 */
public fun Project.desktopTestDependencies(
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kmp.sourceSets
        .named("desktopTest")
        .configure {
            dependencies(configure)
        }
}

/**
 * Adds dependencies to the `iosTest` source set.
 */
public fun Project.iosTestDependencies(
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kmp.sourceSets
        .named("iosTest")
        .configure {
            dependencies(configure)
        }
}
