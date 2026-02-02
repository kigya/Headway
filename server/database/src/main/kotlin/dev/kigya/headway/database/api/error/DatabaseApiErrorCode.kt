package dev.kigya.headway.database.api.error

internal enum class DatabaseApiErrorCode {
    BAD_REQUEST,
    UNAUTHORIZED,
    FORBIDDEN,
    DEPENDENCY_UNAVAILABLE,
    INTERNAL,
}
