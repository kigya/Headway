package dev.kigya.headway.auth.api

import dev.kigya.headway.auth.api.error.AuthApiErrorCode
import dev.kigya.headway.auth.api.error.BadRequestApiException
import dev.kigya.headway.auth.api.error.DependencyUnavailableException
import dev.kigya.headway.auth.api.error.GoogleIdTokenValidationException
import dev.kigya.headway.auth.api.error.InvalidRefreshTokenException
import dev.kigya.headway.auth.api.error.UserNotActiveException
import dev.kigya.headway.auth.api.model.ApiErrorResponse
import dev.kigya.headway.auth.api.port.LoginWithGoogleUseCaseContract
import dev.kigya.headway.auth.api.port.RefreshTokenUseCaseContract
import dev.kigya.headway.auth.api.routing.authRouting
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun Application.installAuthApi(
    loginWithGoogleUseCase: LoginWithGoogleUseCaseContract,
    refreshTokenUseCase: RefreshTokenUseCaseContract,
    basePath: String = "/internal/v1/auth",
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
                    code = AuthApiErrorCode.BAD_REQUEST.name,
                    message = "Bad request",
                ),
            )
        }

        exception<GoogleIdTokenValidationException> { call, _ ->
            call.respond(
                status = HttpStatusCode.Unauthorized,
                message = ApiErrorResponse(
                    code = AuthApiErrorCode.UNAUTHORIZED.name,
                    message = "Unauthorized",
                ),
            )
        }

        exception<InvalidRefreshTokenException> { call, _ ->
            call.respond(
                status = HttpStatusCode.Unauthorized,
                message = ApiErrorResponse(
                    code = AuthApiErrorCode.UNAUTHORIZED.name,
                    message = "Unauthorized",
                ),
            )
        }

        exception<UserNotActiveException> { call, _ ->
            call.respond(
                status = HttpStatusCode.Forbidden,
                message = ApiErrorResponse(
                    code = AuthApiErrorCode.FORBIDDEN.name,
                    message = "Forbidden",
                ),
            )
        }

        exception<DependencyUnavailableException> { call, _ ->
            call.respond(
                status = HttpStatusCode.ServiceUnavailable,
                message = ApiErrorResponse(
                    code = AuthApiErrorCode.DEPENDENCY_UNAVAILABLE.name,
                    message = "Service temporarily unavailable",
                ),
            )
        }

        exception<Throwable> { call, _ ->
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ApiErrorResponse(
                    code = AuthApiErrorCode.INTERNAL.name,
                    message = "Internal server error",
                ),
            )
        }
    }

    routing {
        route(basePath) {
            authRouting(
                loginWithGoogleUseCase = loginWithGoogleUseCase,
                refreshTokenUseCase = refreshTokenUseCase,
            )
        }
    }
}
