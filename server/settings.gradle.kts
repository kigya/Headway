@file:Suppress("UnstableApiUsage")

import java.util.Properties

rootProject.name = "Headway"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
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
        maven {
            url = uri("https://maven.pkg.github.com/kigya/Outcome")
            credentials {
                username = localProperties.getProperty("gpr.user")
                    ?: System.getenv("GPR_USER")
                password = localProperties.getProperty("gpr.key")
                    ?: System.getenv("GPR_KEY")
            }
        }
    }
}

includeBuild("build-logic")

include(
    ":gateway",
    ":common",
    ":database:api",
    ":database:internal",
    ":auth:api",
    ":auth:internal",
)
