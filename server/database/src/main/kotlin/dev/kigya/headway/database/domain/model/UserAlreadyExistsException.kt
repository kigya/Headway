package dev.kigya.headway.database.domain.model

import kotlinx.serialization.Serializable

@Serializable
internal data class UserAlreadyExistsException(
    override val message: String,
    val user: ExposedUser,
) : Exception(message)
