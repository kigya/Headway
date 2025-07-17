import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    with(libs.plugins) {
        with(android) {
            alias(application) apply false
            alias(library) apply false
        }
        with(kotlin) {
            alias(android) apply false
            alias(multiplatform) apply false
            alias(jvm) apply false
        }

        alias(compose) apply false
        alias(compose.compiler) apply false

        alias(ksp) apply false

        alias(detekt) apply false
        alias(google.services) apply false

        alias(serialization) apply false
        alias(room) apply false

        alias(buildkonfig) apply false
    }
}

apply {
    from("config/git/hooks/installer.gradle.kts")
}

/**
 * To execute: ./gradlew assembleRelease -PcomposeCompilerReports=true
 * Failing with minifyReleaseWithR8 issue in Multiplatform, but still generating valid reports.
 * Reports will be regenerated only after changing ui-classes/composables.
 */
subprojects {
    tasks.withType<KotlinCompile>().configureEach {
        val outPath = layout.buildDirectory.dir("compose_compiler").get().asFile.absoluteFile
        compilerOptions {
            if (project.findProperty("composeCompilerReports") == "true") {
                freeCompilerArgs.addAll(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$outPath"
                )
            }
            if (project.findProperty("composeCompilerMetrics") == "true") {
                freeCompilerArgs.addAll(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$outPath"
                )
            }
        }
    }
}
