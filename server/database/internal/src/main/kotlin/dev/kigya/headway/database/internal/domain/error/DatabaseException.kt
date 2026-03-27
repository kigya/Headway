package dev.kigya.headway.database.internal.domain.error

import dev.kigya.headway.database.api.model.DatabasePreparationErrorCodes
import dev.kigya.headway.database.api.model.out.DatabaseUser

sealed class DatabaseException(
    override val message: String,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause) {

    data class InvalidRequest(
        override val message: String,
        override val cause: Throwable? = null,
    ) : DatabaseException(message, cause)

    data class Unauthorized(
        override val message: String = "Unauthorized",
        override val cause: Throwable? = null,
    ) : DatabaseException(message, cause)

    open class Forbidden(
        override val message: String = "Forbidden",
        override val cause: Throwable? = null,
    ) : DatabaseException(message, cause)

    data class UserNotInvited(
        override val message: String = "User is not invited",
    ) : Forbidden(message)

    open class Conflict(
        override val message: String = "Conflict",
        override val cause: Throwable? = null,
    ) : DatabaseException(message, cause)

    data class UserAlreadyExists(
        val user: DatabaseUser,
        override val message: String = "User already exists",
    ) : Conflict(message)

    data class DependencyUnavailable(
        val dependency: String,
        override val message: String = "Dependency unavailable: $dependency",
        override val cause: Throwable? = null,
    ) : DatabaseException(message, cause)

    data class NotFound(
        override val message: String = "Not found",
        override val cause: Throwable? = null,
    ) : DatabaseException(message, cause)

    data class PreparationForbidden(
        override val message: String = "Preparation not allowed for this principal",
        override val cause: Throwable? = null,
    ) : Forbidden(message, cause)

    data class PreparationConflict(
        override val message: String,
        override val cause: Throwable? = null,
    ) : Conflict(message, cause)

    data class PreparationScopeSessionClosed(
        override val message: String = DatabasePreparationErrorCodes.SCOPE_SESSION_CLOSED,
        override val cause: Throwable? = null,
    ) : Conflict(message, cause)
}
