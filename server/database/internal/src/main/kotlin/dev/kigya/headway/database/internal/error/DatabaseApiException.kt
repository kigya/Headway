package dev.kigya.headway.database.internal.error

import dev.kigya.headway.database.api.model.out.DatabaseUser
import kotlinx.serialization.Serializable

internal class UserNotInvitedException : Exception("User is not invited")
internal class SessionDoesNotExistsException : Exception("Session does not exists")
internal class SessionValidationException(message: String) : Exception(message)

@Serializable
internal data class UserAlreadyExistsException(
    override val message: String,
    val user: DatabaseUser,
) : Exception(message)
