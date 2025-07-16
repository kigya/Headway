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
        google()
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/kigya/Outcome")
            credentials {
                username = localProperties.getProperty("gpr.user")
                    ?: System.getenv("GITHUB_ACTOR")
                password = localProperties.getProperty("gpr.key")
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

includeBuild("build-logic")

include(":composeApp")
include(":server")
include(":shared")
