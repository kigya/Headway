package dev.kigya.headway.auth.internal.presentation

import dev.kigya.headway.auth.internal.domain.usecase.LoginWithGoogleUseCase
import dev.kigya.headway.auth.internal.domain.usecase.RefreshTokenUseCase
import dev.kigya.headway.auth.internal.presentation.plugin.authRouting
import dev.kigya.headway.auth.internal.presentation.plugin.authStatusPages
import dev.kigya.headway.common.extension.defaultContentNegotiation
import io.ktor.server.application.Application

internal fun Application.installAuthApi(
    loginWithGoogle: LoginWithGoogleUseCase,
    refreshToken: RefreshTokenUseCase,
) {
    defaultContentNegotiation()
    authStatusPages()
    authRouting(
        loginWithGoogle = loginWithGoogle,
        refreshToken = refreshToken,
    )
}
