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
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

includeBuild("build-logic")

include(
    ":app:headwayAndroid",
    ":app:headwayDesktop",
    ":app:headwayWeb",

    ":shared",

    ":feature:splash:api",
    ":feature:splash:internal",
    ":feature:splash:di",

    ":feature:auth:api",
    ":feature:auth:internal",
    ":feature:auth:di",

    ":navigation:api",
    ":navigation:internal",
    ":navigation:di",

    ":di:api",
    ":di:modules",

    "core:annotation",
    "core:design-system",
    "core:outcome",
)
