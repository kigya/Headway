package dev.kigya.headway.home.internal.domain.error

sealed class HomeException(
    override val message: String,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause) {

    data class InvalidRequest(
        override val message: String,
        override val cause: Throwable? = null,
    ) : HomeException(message, cause)
}
