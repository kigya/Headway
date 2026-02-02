package dev.kigya.headway.database.api.error

import dev.kigya.headway.database.api.model.ExposedUser
import kotlinx.serialization.Serializable

internal class BadRequestApiException : RuntimeException()
internal class UserNotInvitedException : Exception("User is not invited")
internal class SessionDoesNotExistsException : Exception("Session does not exists")
internal class SessionValidationException(message: String) : Exception(message)

@Serializable
internal data class UserAlreadyExistsException(
    override val message: String,
    val user: ExposedUser,
) : Exception(message)
