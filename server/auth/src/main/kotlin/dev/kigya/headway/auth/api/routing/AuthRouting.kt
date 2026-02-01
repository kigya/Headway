package dev.kigya.headway.auth.api.routing

import dev.kigya.headway.auth.api.port.LoginWithGoogleUseCaseContract
import dev.kigya.headway.auth.api.port.RefreshTokenUseCaseContract
import io.ktor.server.routing.Route

internal fun Route.authRouting(
    loginWithGoogleUseCase: LoginWithGoogleUseCaseContract,
    refreshTokenUseCase: RefreshTokenUseCaseContract,
) {
    healthzRoute()
    authByGoogle(loginWithGoogleUseCase)
    refreshToken(refreshTokenUseCase)
}
