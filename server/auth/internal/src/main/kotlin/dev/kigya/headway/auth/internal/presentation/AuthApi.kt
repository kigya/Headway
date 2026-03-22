package dev.kigya.headway.auth.internal.presentation

import dev.kigya.headway.auth.internal.domain.usecase.LoginWithGoogleUseCase
import dev.kigya.headway.auth.internal.domain.usecase.RefreshTokenUseCase
import dev.kigya.headway.auth.internal.domain.usecase.ValidateAccessTokenUseCase
import dev.kigya.headway.auth.internal.presentation.plugin.authRouting
import dev.kigya.headway.auth.internal.presentation.plugin.authStatusPages
import dev.kigya.headway.common.extension.defaultContentNegotiation
import dev.kigya.headway.common.extension.defaultResources
import io.ktor.server.application.Application

internal fun Application.installAuthApi(
    loginWithGoogle: LoginWithGoogleUseCase,
    refreshToken: RefreshTokenUseCase,
    validateAccessToken: ValidateAccessTokenUseCase,
) {
    defaultContentNegotiation()
    defaultResources()
    authStatusPages()
    authRouting(
        loginWithGoogle = loginWithGoogle,
        refreshToken = refreshToken,
        validateAccessToken = validateAccessToken,
    )
}
