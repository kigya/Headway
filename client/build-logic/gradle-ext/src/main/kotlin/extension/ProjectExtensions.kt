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
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

/**
 * Allows configuring an extension of the given [type] if it exists on the project. Prints a message
 * if the extension is not found.
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
    extensions.findByType(type)?.apply(config) ?: run { println("Type not found $type") }
}

/** A type-safe accessor for the version catalog "libs" on the root project. */
public inline val Project.libs: LibrariesForLibs
    get() = (this as ExtensionAware).extensions.getByName("libs") as LibrariesForLibs

/** Shortcut to retrieve the Kotlin Multiplatform extension from the project. */
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
    kmp.sourceSets.named("commonMain").configure { dependencies(configure) }
}

/** Adds dependencies to the `androidMain` source set. */
public fun Project.androidMainDependencies(
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kmp.sourceSets.named("androidMain").configure { dependencies(configure) }
}

/** Adds dependencies to the `desktopMain` source set. */
public fun Project.desktopMainDependencies(
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kmp.sourceSets.named("desktopMain").configure { dependencies(configure) }
}

/** Adds dependencies to the `iosMain` source set. */
public fun Project.iosMainDependencies(
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kmp.sourceSets.named("iosMain").configure { dependencies(configure) }
}

/** Adds dependencies to the `jsMain` source set. */
public fun Project.wasmMainDependencies(
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kmp.sourceSets.named("wasmJsMain").configure { dependencies(configure) }
}

/** Adds dependencies to the `commonTest` source set. */
public fun Project.commonTestDependencies(
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kmp.sourceSets.named("commonTest").configure { dependencies(configure) }
}

/** Adds dependencies to the `androidTest` source set. */
public fun Project.androidTestDependencies(
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kmp.sourceSets.named("androidTest").configure { dependencies(configure) }
}

/** Adds dependencies to the `desktopTest` source set. */
public fun Project.desktopTestDependencies(
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kmp.sourceSets.named("desktopTest").configure { dependencies(configure) }
}

/** Adds dependencies to the `iosTest` source set. */
public fun Project.iosTestDependencies(
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kmp.sourceSets.named("iosTest").configure { dependencies(configure) }
}

/** Adds dependencies to the `jsTest` source set. */
public fun Project.wasmTestDependencies(
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kmp.sourceSets.named("wasmJsTest").configure { dependencies(configure) }
}

/**
 * Executes [action] when the Kotlin/JVM plugin is applied to the project.
 *
 * Useful for safely configuring JVM-only Kotlin options from convention plugins without requiring
 * all modules to apply Kotlin/JVM.
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
 * Useful for safely configuring KMP options from convention plugins without requiring non-KMP
 * modules to apply Kotlin Multiplatform.
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
 * Enables Kotlin compiler language feature "ContextParameters" via `-Xcontext-parameters` for both
 * Kotlin/JVM and Kotlin Multiplatform modules (whichever is applied).
 *
 * This is a convenience wrapper around [withKotlinJvmExtension] and [withKotlinKmpExtension].
 *
 * Note: this does not turn on K2 mode; it only adds compiler arguments.
 */
public fun Project.enableContextParameters() {
    withKotlinJvmExtension { compilerOptions { freeCompilerArgs.add("-Xcontext-parameters") } }

    withKotlinKmpExtension { compilerOptions { freeCompilerArgs.add("-Xcontext-parameters") } }
}

/** Adds a list of Kotlin compiler opt-ins project-wide for both Kotlin/JVM and KMP modules. */
public fun Project.addKotlinCompilerOptIns(optIns: Iterable<String>) {
    withKotlinJvmExtension { compilerOptions { this.optIn.addAll(optIns) } }

    withKotlinKmpExtension { compilerOptions { this.optIn.addAll(optIns) } }
}

/**
 * Enables Kotlin compiler flag `-Xdata-flow-based-exhaustiveness` and adds
 * `kotlin.contracts.ExperimentalExtendedContracts` opt-in.
 */
public fun Project.enableDataFlowBasedExhaustiveness() {
    val optIn = "kotlin.contracts.ExperimentalExtendedContracts"
    val flag = "-Xdata-flow-based-exhaustiveness"

    withKotlinJvmExtension {
        compilerOptions {
            this.optIn.add(optIn)
            freeCompilerArgs.add(flag)
        }
    }

    withKotlinKmpExtension {
        compilerOptions {
            this.optIn.add(optIn)
            freeCompilerArgs.add(flag)
        }
    }
}

/** Enables Kotlin compiler flag `-Xexplicit-backing-field`. */
public fun Project.enableExplicitBackingFields() {
    val flag = "-Xexplicit-backing-fields"

    withKotlinJvmExtension { compilerOptions { freeCompilerArgs.add(flag) } }

    withKotlinKmpExtension { compilerOptions { freeCompilerArgs.add(flag) } }
}

/**
 * Dependency DSL entry-point for server microservice modules.
 *
 * This is a semantic alias of Gradle's `dependencies { ... }`, kept to make build scripts more
 * declarative and consistent with other project conventions.
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
 * This is a semantic alias of Gradle's `dependencies { ... }`, kept to make build scripts more
 * declarative and consistent with other project conventions.
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
