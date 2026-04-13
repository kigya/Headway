@file:Suppress("UnstableApiUsage")

import java.util.Properties

rootProject.name = "Headway"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

val headwayGithubPluginManagementScript =
    sequenceOf(
        rootDir.resolve("../config/gradle/headway-github-env-sync-plugin-management.settings.gradle.kts"),
        rootDir.resolve("config/gradle/headway-github-env-sync-plugin-management.settings.gradle.kts"),
    ).firstOrNull { it.isFile }
        ?: error("Missing headway-github-env-sync plugin management script")

apply(from = headwayGithubPluginManagementScript)

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

val localProperties: Properties = Properties().apply {
    rootDir
        .resolve("local.properties")
        .takeIf { it.exists() }
        ?.inputStream()
        ?.use { load(it) }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

include(
    ":admin:api",
    ":admin:internal",
    ":gateway",
    ":common",
    ":database:api",
    ":database:internal",
    ":auth:api",
    ":auth:internal",
    ":home:api",
    ":home:internal",
)
