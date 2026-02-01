package dev.kigya.headway.common.extension

fun stringEnv(name: String): String =
    requireNotNull(System.getenv(name)) { "Missing env var: $name" }

fun intEnv(name: String): Int =
    requireNotNull(System.getenv(name)) { "Missing env var: $name" }.toIntOrNull()
        ?: error("Invalid env var: $name")

fun longEnv(name: String): Long =
    requireNotNull(System.getenv(name)) { "Missing env var: $name" }.toLongOrNull()
        ?: error("Invalid env var: $name")

