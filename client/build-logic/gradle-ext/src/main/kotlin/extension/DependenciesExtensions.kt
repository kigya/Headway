@file:Suppress("NOTHING_TO_INLINE")

package extension

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.catalog.TypeSafeProjectDependencyFactory
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

/**
 * Adds a dependency to the 'implementation' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
public inline fun DependencyHandler.implementation(dependencyNotation: Any): Dependency? =
    add("implementation", dependencyNotation)

/**
 * Adds a dependency to the 'debugImplementation' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
public inline fun DependencyHandler.debugImplementation(dependencyNotation: Any): Dependency? =
    add("debugImplementation", dependencyNotation)

/**
 * Adds a dependency to the 'ksp' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
public inline fun DependencyHandler.ksp(dependencyNotation: Any): Dependency? =
    add("ksp", dependencyNotation)


/**
 * Adds a dependency to the 'testImplementation' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
public inline fun DependencyHandler.testImplementation(dependencyNotation: Any): Dependency? =
    add("testImplementation", dependencyNotation)

/**
 * Adds a dependency to the 'androidTestImplementation' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
public inline fun DependencyHandler.androidTestImplementation(dependencyNotation: Any): Dependency? =
    add("androidTestImplementation", dependencyNotation)

/**
 * Adds a dependency to the 'detektPlugins' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
public inline fun DependencyHandler.detektPlugins(dependencyNotation: Any): Dependency? =
    add("detektPlugins", dependencyNotation)


/**
 * Provides access to the version catalog defined in `libs.versions.toml` (type-safe accessors).
 *
 * This allows you to reference dependencies using the `libs` alias block.
 *
 * Example:
 * ```
 * dependencies {
 *     libs {
 *         implementation(kotlin.stdlib)
 *     }
 * }
 * ```
 *
 * @param block A lambda with receiver of [LibrariesForLibs] to access catalog dependencies.
 */
public fun KotlinDependencyHandler.libs(block: LibrariesForLibs.() -> Unit) {
    project.libs.block()
}

/**
 * Provides access to project dependencies defined in the root project's `settings.gradle.kts` or included builds.
 *
 * This allows type-safe access to subprojects via the `projects` block.
 *
 * Example:
 * ```
 * dependencies {
 *     projects {
 *         implementation(gradle-ext)
 *     }
 * }
 * ```
 *
 * @param block A lambda with receiver of [TypeSafeProjectDependencyFactory] to access subprojects.
 */
public fun KotlinDependencyHandler.projects(block: TypeSafeProjectDependencyFactory.() -> Unit) {
    ((project as ExtensionAware).extensions.getByName("projects") as TypeSafeProjectDependencyFactory).block()
}
