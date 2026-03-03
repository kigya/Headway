package dev.kigya.headway.common.util

fun stringEnv(name: String): String =
    requireNotNull(System.getenv(name)) { "Missing string env var: $name" }

fun intEnv(name: String): Int =
    requireNotNull(System.getenv(name)) { "Missing int env var: $name" }.toIntOrNull()
        ?: error("Invalid env var: $name")

fun longEnv(name: String): Long =
    requireNotNull(System.getenv(name)) { "Missing long env var: $name" }.toLongOrNull()
        ?: error("Invalid env var: $name")
