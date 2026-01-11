package dev.kigya.headway.database.api.error

import kotlinx.serialization.Serializable
import dev.kigya.headway.database.api.model.ExposedUser

@Serializable
internal data class UserAlreadyExistsException(
    override val message: String,
    val user: ExposedUser,
) : Exception(message)
