package dev.kigya.headway.database.api

import dev.kigya.headway.database.api.error.BadRequestApiException
import dev.kigya.headway.database.api.error.DatabaseApiErrorCode
import dev.kigya.headway.database.api.error.SessionDoesNotExistsException
import dev.kigya.headway.database.api.error.SessionValidationException
import dev.kigya.headway.database.api.error.UserAlreadyExistsException
import dev.kigya.headway.database.api.error.UserNotInvitedException
import dev.kigya.headway.database.api.model.ApiErrorResponse
import dev.kigya.headway.database.api.port.CreateGoogleUserUseCaseContract
import dev.kigya.headway.database.api.port.CreateSessionUseCaseContract
import dev.kigya.headway.database.api.port.GetUserUseCaseContract
import dev.kigya.headway.database.api.port.InviteUserUseCaseContract
import dev.kigya.headway.database.api.port.UpsertGoogleUserUseCaseContract
import dev.kigya.headway.database.api.port.ValidateSessionUseCaseContract
import dev.kigya.headway.database.api.routing.databaseRouting
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun Application.installDatabaseApi(
    getUserUseCase: GetUserUseCaseContract,
    createUserUseCase: CreateGoogleUserUseCaseContract,
    upsertGoogleUserUseCase: UpsertGoogleUserUseCaseContract,
    createSessionUseCase: CreateSessionUseCaseContract,
    validateSessionUseCase: ValidateSessionUseCaseContract,
    inviteUserUseCase: InviteUserUseCaseContract,
    basePath: String = "/internal/v1/database",
) {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = false
                encodeDefaults = false
            }
        )
    }

    install(StatusPages) {
        exception<BadRequestApiException> { call, _ ->
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ApiErrorResponse(
                    code = DatabaseApiErrorCode.BAD_REQUEST.name,
                    message = "Bad request",
                ),
            )
        }

        exception<SessionValidationException> { call, _ ->
            call.respond(
                status = HttpStatusCode.Unauthorized,
                message = ApiErrorResponse(
                    code = DatabaseApiErrorCode.UNAUTHORIZED.name,
                    message = "Unauthorized",
                ),
            )
        }

        exception<SessionDoesNotExistsException> { call, _ ->
            call.respond(
                status = HttpStatusCode.Unauthorized,
                message = ApiErrorResponse(
                    code = DatabaseApiErrorCode.UNAUTHORIZED.name,
                    message = "Unauthorized",
                ),
            )
        }

        exception<UserNotInvitedException> { call, _ ->
            call.respond(
                status = HttpStatusCode.Forbidden,
                message = ApiErrorResponse(
                    code = DatabaseApiErrorCode.FORBIDDEN.name,
                    message = "Forbidden",
                ),
            )
        }

        exception<UserAlreadyExistsException> { call, e ->
            call.respond(status = HttpStatusCode.Conflict, message = e.user)
        }

        exception<Throwable> { call, t ->
            this@installDatabaseApi.log.error("Unhandled error", t)
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ApiErrorResponse(
                    code = DatabaseApiErrorCode.INTERNAL.name,
                    message = "Internal server error",
                ),
            )
        }
    }

    routing {
        route(basePath) {
            databaseRouting(
                getUser = getUserUseCase,
                createUser = createUserUseCase,
                upsertGoogleUser = upsertGoogleUserUseCase,
                createSession = createSessionUseCase,
                validateSession = validateSessionUseCase,
                inviteUser = inviteUserUseCase,
            )
        }
    }
}
