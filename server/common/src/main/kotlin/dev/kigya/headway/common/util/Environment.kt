package dev.kigya.headway.common.util

enum class Environment(val rawValue: String) {
    DEV("dev"),
    PROD("prod"),
    ;

    val isDev: Boolean
        get() = this == DEV

    val isProd: Boolean
        get() = this == PROD

    companion object {
        fun from(rawValue: String?): Environment {
            val normalized = rawValue?.trim()?.lowercase()
            require(!normalized.isNullOrBlank()) {
                "Environment value is missing or blank; set ENV to dev or prod"
            }
            return entries.firstOrNull { it.rawValue == normalized }
                ?: throw IllegalArgumentException(
                    "Invalid ENV: $rawValue. Expected one of: ${entries.joinToString { it.rawValue }}",
                )
        }
    }
}

fun environmentEnv(name: String): Environment = Environment.from(System.getenv(name))

fun stringEnv(name: String): String =
    requireNotNull(System.getenv(name)) { "Missing string env var: $name" }

fun intEnv(name: String): Int =
    requireNotNull(System.getenv(name)) { "Missing int env var: $name" }.toIntOrNull()
        ?: error("Invalid env var: $name")

fun longEnv(name: String): Long =
    requireNotNull(System.getenv(name)) { "Missing long env var: $name" }.toLongOrNull()
        ?: error("Invalid env var: $name")
