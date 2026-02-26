package extension

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

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
 * Executes [action] when the Kotlin/JVM plugin is applied to the project.
 *
 * Useful for safely configuring JVM-only Kotlin options from convention plugins without
 * requiring all modules to apply Kotlin/JVM.
 *
 * Usage:
 * ```
 * withKotlinJvmExtension {
 *   compilerOptions { /* ... */ }
 * }
 * ```
 */
private fun Project.withKotlinJvmExtension(action: Action<KotlinJvmProjectExtension>) {
    plugins.withId("org.jetbrains.kotlin.jvm") {
        the<KotlinJvmProjectExtension>().apply { action.execute(this) }
    }
}

/**
 * Executes [action] when the Kotlin Multiplatform plugin is applied to the project.
 *
 * Useful for safely configuring KMP options from convention plugins without requiring
 * non-KMP modules to apply Kotlin Multiplatform.
 *
 * Usage:
 * ```
 * withKotlinKmpExtension {
 *   sourceSets { /* ... */ }
 * }
 * ```
 */
private fun Project.withKotlinKmpExtension(action: Action<KotlinMultiplatformExtension>) {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        the<KotlinMultiplatformExtension>().apply { action.execute(this) }
    }
}

/**
 * Enables Kotlin compiler language feature "ContextParameters" via `-Xcontext-parameters`
 * for both Kotlin/JVM and Kotlin Multiplatform modules (whichever is applied).
 *
 * This is a convenience wrapper around [withKotlinJvmExtension] and [withKotlinKmpExtension].
 *
 * Note: this does not turn on K2 mode; it only adds compiler arguments.
 */
public fun Project.enableContextParameters() {
    withKotlinJvmExtension {
        compilerOptions {
            freeCompilerArgs.add("-Xcontext-parameters")
        }
    }

    withKotlinKmpExtension {
        compilerOptions {
            freeCompilerArgs.add("-Xcontext-parameters")
        }
    }
}

/**
 * Dependency DSL entry-point for server microservice modules.
 *
 * This is a semantic alias of Gradle's `dependencies { ... }`, kept to make build scripts
 * more declarative and consistent with other project conventions.
 *
 * Usage:
 * ```
 * microserviceDependencies {
 *   implementation(libs.ktor.serverCore)
 * }
 * ```
 */
public fun Project.microserviceDependencies(block: DependencyHandlerScope.() -> Unit) {
    dependencies(block)
}

/**
 * Dependency DSL entry-point for JVM library modules.
 *
 * This is a semantic alias of Gradle's `dependencies { ... }`, kept to make build scripts
 * more declarative and consistent with other project conventions.
 *
 * Usage:
 * ```
 * jvmLibraryDependencies {
 *   api(libs.some.library)
 * }
 * ```
 */
public fun Project.jvmLibraryDependencies(block: DependencyHandlerScope.() -> Unit) {
    dependencies(block)
}
