@file:Suppress("UnstableApiUsage")

import java.util.Properties

rootProject.name = "Headway"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/kigya/GithubEnvSync-Plugin")
            credentials {
                username = "kigya"
                password = "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"
            }
        }
    }
    includeBuild("build-logic")
}

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
    ":gateway",
    ":common",
    ":database:api",
    ":database:internal",
    ":auth:api",
    ":auth:internal",
)
