package dev.kigya.headway.di.api.module

import java.io.File

internal object DesktopHeadwayEnv {

    private val loadedValues: Map<String, String> by lazy { loadFromFiles() }

    fun warmUp() {
        loadedValues
    }

    fun get(name: String): String? {
        System.getenv(name)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.let { return it }
        return loadedValues[name]
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
    }

    private fun loadFromFiles(): Map<String, String> {
        val envFile = resolveEnvFile() ?: return emptyMap()
        return parseEnvFile(envFile)
    }

    private fun resolveEnvFile(): File? {
        sequenceOf(
            System.getenv(HEADWAY_DESKTOP_ENV_FILE_KEY),
            System.getProperty(HEADWAY_DESKTOP_ENV_FILE_PROPERTY),
        ).mapNotNull { path ->
            path?.trim()?.takeIf { it.isNotEmpty() }?.let(::File)
        }.firstOrNull { it.isFile }?.let { return it }

        findEnvFileWalkingUp()?.let { return it }
        return findHomeEnvFile()
    }

    private fun findEnvFileWalkingUp(): File? {
        val envName = resolveEnvName()
        var directory = File(System.getProperty("user.dir"))
        repeat(WALK_UP_MAX_DEPTH) {
            sequenceOf(
                File(directory, "client/secrets/$envName/env.desktop"),
                File(directory, "secrets/$envName/env.desktop"),
            ).firstOrNull { candidate -> candidate.isFile }?.let { return it }
            directory = directory.parentFile ?: return null
        }
        return null
    }

    private fun findHomeEnvFile(): File? {
        val homeDirectory = System.getProperty(USER_HOME_PROPERTY).orEmpty()
        return sequenceOf(
            File(homeDirectory, HOME_HEADWAY_ENV_FILE),
            File(homeDirectory, HOME_CONFIG_HEADWAY_ENV_FILE),
        ).firstOrNull { candidate -> candidate.isFile }
    }

    private fun resolveEnvName(): String = sequenceOf(
        System.getenv(HEADWAY_DESKTOP_ENV_KEY),
        System.getProperty(HEADWAY_DESKTOP_ENV_PROPERTY),
    ).mapNotNull { value ->
        value?.trim()?.takeIf { it.isNotEmpty() }
    }.firstOrNull() ?: DEFAULT_ENV_NAME

    private fun parseEnvFile(file: File): Map<String, String> = buildMap {
        file.readLines().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                return@forEach
            }
            val equalsIndex = trimmed.indexOf('=')
            if (equalsIndex <= 0) {
                return@forEach
            }
            val key = trimmed.substring(0, equalsIndex).trim()
            val rawValue = trimmed.substring(equalsIndex + 1).trim()
            put(key, unwrapEnvValue(rawValue))
        }
    }

    private fun unwrapEnvValue(rawValue: String): String = when {
        rawValue.startsWith('"') && rawValue.endsWith('"') && rawValue.length >= 2 ->
            rawValue.substring(1, rawValue.lastIndex)
        rawValue.startsWith('\'') && rawValue.endsWith('\'') && rawValue.length >= 2 ->
            rawValue.substring(1, rawValue.lastIndex)
        else -> rawValue
    }
}

private const val HEADWAY_DESKTOP_ENV_FILE_KEY: String = "HEADWAY_DESKTOP_ENV_FILE"

private const val HEADWAY_DESKTOP_ENV_FILE_PROPERTY: String = "headway.desktop.env.file"

private const val HEADWAY_DESKTOP_ENV_KEY: String = "HEADWAY_DESKTOP_ENV"

private const val HEADWAY_DESKTOP_ENV_PROPERTY: String = "headwayDesktopEnv"

private const val DEFAULT_ENV_NAME: String = "dev"

private const val WALK_UP_MAX_DEPTH: Int = 12

private const val USER_HOME_PROPERTY: String = "user.home"

private const val HOME_HEADWAY_ENV_FILE: String = ".headway/env.desktop"

private const val HOME_CONFIG_HEADWAY_ENV_FILE: String = ".config/headway/env.desktop"
