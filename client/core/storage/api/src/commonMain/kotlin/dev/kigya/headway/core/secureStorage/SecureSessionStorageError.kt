package dev.kigya.headway.core.secureStorage

sealed interface SecureSessionStorageError {
    data class OperationFailed(
        val cause: Throwable,
    ) : SecureSessionStorageError
}
