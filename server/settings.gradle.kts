@file:Suppress("UnstableApiUsage")

import java.util.Properties

rootProject.name = "Headway"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    fun decryptToken(
        hexInput: String,
        key: String,
    ): String {
        return hexInput.chunked(2).mapIndexed { i, hexChar ->
            val originalChar = hexChar.toInt(16) xor key[i % key.length].code
            originalChar.toChar()
        }.joinToString("")
    }

    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/kigya/GithubEnvSync-Plugin")
            credentials {
                username = providers.gradleProperty("github.env.sync.username").getOrNull()
                password = providers
                    .gradleProperty("github.env.sync.token")
                    .getOrNull()
                    ?.let { encryptedXex ->
                        val key = providers
                            .gradleProperty("github.env.sync.token.key")
                            .getOrNull()
                        requireNotNull(key) {
                            "Github token decryption key (github.env.sync.token.key) is required"
                        }

                        decryptToken(encryptedXex, key)
                    }
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
