import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Android
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidKmpLibrary) apply false

    // Kotlin
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinJvm) apply false

    // Compose
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.composeKotlin) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.buildkonfig) apply false
}

buildscript {
    dependencies {
        classpath(libs.gradle.kotlin)
    }
}

apply {
    from("../config/git/hooks/installer.gradle.kts")
    from("../config/templates/installer.gradle.kts")
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
            freeCompilerArgs.add("-Xskip-prerelease-check")
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
