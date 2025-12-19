package dev.kigya.headway.database.routing.user

import dev.kigya.headway.database.domain.model.UserAlreadyExistsException
import dev.kigya.headway.database.domain.service.UsersService
import dev.kigya.headway.database.routing.user.request.CreateUserRequestDto
import ext.respondBadRequest
import ext.respondServerError
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import java.util.UUID

/** The key for the user ID path parameter. */
private const val KEY_USER_ID = "user_id"

/** The key for the user Google ID query parameter. */
private const val KEY_USER_GOOGLE_ID = "google_id"

/**
 * Defines the routing for user-related endpoints.
 * @param usersService The service for handling user data operations.
 */
internal fun Route.usersRouting(usersService: UsersService) {
    getUserById(usersService)
    createUser(usersService)
}

/**
 * Defines the GET endpoint for retrieving a user by their ID.
 * It handles requests to `/user` with a `google_id` query parameter.
 * - Responds with the user object on success.
 * - Responds with BadRequest if the 'google_id' parameter is missing.
 * - Responds with NotFound if the user does not exist.
 * @param usersService The service for handling user data operations.
 */
private fun Route.getUserById(usersService: UsersService) {
    get("/user") {
        val googleId = call.request.queryParameters[KEY_USER_GOOGLE_ID]
        val userId = call.request.queryParameters[KEY_USER_ID]
        if (googleId.isNullOrBlank() && userId.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Query parameter '$KEY_USER_GOOGLE_ID' or '$KEY_USER_ID' is missing or empty.")
            return@get
        }

        try {
            val user = when {
                googleId != null -> usersService.readByGoogleId(googleId)
                userId != null -> usersService.readById(UUID.fromString(userId))
                else -> null
            }
            if (user != null) {
                call.respond(HttpStatusCode.OK, user)
            } else {
                call.respond(HttpStatusCode.NotFound, "User with Google ID '$googleId' not found.")
            }
        } catch (e: Exception) {
            call.application.environment.log.error("Failed to retrieve user by Google ID", e)
            call.respondServerError(e)
        }
    }
}

/**
 * Defines the POST endpoint to create a new user.
 * It handles requests to `/user`.
 * - Responds with the created user object and a Location header on success (201 Created).
 * - Responds with BadRequest if the request body is invalid.
 * - Responds with Conflict if a user with the same Google ID or email already exists.
 * - Responds with InternalServerError for any other failures.
 * @param usersService The service for handling user data operations.
 */
private fun Route.createUser(usersService: UsersService) {
    // Define the POST route for creating a user.
    post("/user") {
        // Deserialize the request body into a DTO, handling potential errors.
        val request = try {
            call.receive<CreateUserRequestDto>()
        } catch (e: Exception) {
            call.respondBadRequest(e)
            return@post
        }

        try {
            // Call the service to create a new user with the provided data.
            val newUser = usersService.insertGoogleUser(
                googleId = request.googleId,
                email = request.email,
                name = request.name,
                avatarUrl = request.avatarUrl,
                role = request.role,
            )

            // On successful creation, set the Location header to the new user's resource URL.
            call.response.header(HttpHeaders.Location, "/api/v1/users/${newUser.id}")
            // Respond with 201 Created and the new user object.
            call.respond(HttpStatusCode.Created, newUser)
        } catch (e: UserAlreadyExistsException) {
            // If the user already exists, respond with 409 Conflict.
            call.respond(
                status = HttpStatusCode.Conflict,
                message = e.user,
            )
        } catch (e: Exception) {
            // For any other unexpected errors, log the exception and respond with 500 Internal Server Error.
            call.application.environment.log.error("Failed to create user", e)
            call.respondServerError(e)
        }
    }
}
