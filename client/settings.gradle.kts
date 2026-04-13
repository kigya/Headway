@file:Suppress("UnstableApiUsage")

import java.util.Properties

rootProject.name = "Headway"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

apply(from = rootDir.resolve("../config/gradle/headway-github-env-sync-plugin-management.settings.gradle.kts"))

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
        google()
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

    "feature:splash-api",
    "feature:splash-internal",
    "feature:splash-di",

    "feature:auth-api",
    "feature:auth-internal",
    "feature:auth-di",

    "navigation:navigation-api",
    "navigation:navigation-internal",
    "navigation:navigation-di",

    "di:di-api",
    "di:di-modules",

    "core:annotation",
    "core:network-api",
    "core:apollo",
    "core:design-system",
    "core:outcome",
    "core:storage-api",
    "core:storage-secure",
    "core:session-api",
    "core:session-internal",

    "feature:home-api",
    "feature:home-internal",
    "feature:home-di",

    "feature:learn-questions-api",
    "feature:learn-questions-internal",
    "feature:learn-questions-di",
)

project(":core:network-api").projectDir = file("core/network/api")
project(":core:storage-api").projectDir = file("core/storage/api")
project(":core:storage-secure").projectDir = file("core/storage/secure")
project(":core:session-api").projectDir = file("core/session/api")
project(":core:session-internal").projectDir = file("core/session/internal")

project(":feature:splash-api").projectDir = file("feature/splash/api")
project(":feature:splash-internal").projectDir = file("feature/splash/internal")
project(":feature:splash-di").projectDir = file("feature/splash/di")

project(":feature:auth-api").projectDir = file("feature/auth/api")
project(":feature:auth-internal").projectDir = file("feature/auth/internal")
project(":feature:auth-di").projectDir = file("feature/auth/di")

project(":feature:home-api").projectDir = file("feature/home/api")
project(":feature:home-internal").projectDir = file("feature/home/internal")
project(":feature:home-di").projectDir = file("feature/home/di")

project(":feature:learn-questions-api").projectDir = file("feature/learn-questions/api")
project(":feature:learn-questions-internal").projectDir = file("feature/learn-questions/internal")
project(":feature:learn-questions-di").projectDir = file("feature/learn-questions/di")

project(":navigation:navigation-api").projectDir = file("navigation/api")
project(":navigation:navigation-internal").projectDir = file("navigation/internal")
project(":navigation:navigation-di").projectDir = file("navigation/di")

project(":di:di-api").projectDir = file("di/api")
project(":di:di-modules").projectDir = file("di/modules")
