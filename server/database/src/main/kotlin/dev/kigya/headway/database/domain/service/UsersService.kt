package dev.kigya.headway.database.domain.service

import dev.kigya.headway.database.domain.model.ExposedUser
import dev.kigya.headway.database.domain.model.ExposedUserRole
import dev.kigya.headway.database.domain.model.UserAlreadyExistsException
import java.util.UUID

/**
 * A service for managing user data within the database.
 * It provides methods for creating, reading, and updating user records.
 */
internal interface UsersService {

    /**
     * Retrieves a user by their unique identifier.
     * @param id The UUID of the user to find.
     * @return The [ExposedUser] if found, otherwise null.
     */
    suspend fun readById(id: UUID): ExposedUser?

    /**
     * Retrieves a user by their unique Google ID.
     * @param googleId The Google ID of the user to find.
     * @return The [ExposedUser] if found, otherwise null.
     */
    suspend fun readByGoogleId(googleId: String): ExposedUser?

    /**
     * Retrieves a user by their email address.
     * @param email The email of the user to find.
     * @return The [ExposedUser] if found, otherwise null.
     */
    suspend fun readByEmail(email: String): ExposedUser?

    /**
     * Creates a new user record for a user authenticating via Google.
     * @param googleId The user's unique Google ID.
     * @param email The user's email address.
     * @param name The user's display name.
     * @param avatarUrl An optional URL for the user's avatar image.
     * @param role The role to assign to the new user.
     * @return The newly created [ExposedUser].
     * @throws UserAlreadyExistsException if a user with the same Google ID already exists.
     */
    suspend fun insertGoogleUser(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
        role: ExposedUserRole,
    ): ExposedUser

    /**
     * Updates the name and/or avatar for an existing user.
     * @param userId The unique identifier of the user to update.
     * @param name The new display name for the user.
     * @param avatarUrl The new optional URL for the user's avatar image.
     * @return The updated [ExposedUser] if the operation was successful, otherwise null.
     */
    suspend fun updateGoogleUser(
        userId: UUID,
        name: String,
        avatarUrl: String?
    ): ExposedUser?
}
