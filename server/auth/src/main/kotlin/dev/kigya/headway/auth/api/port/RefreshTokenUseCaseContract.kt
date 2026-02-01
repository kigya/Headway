package dev.kigya.headway.auth.api.port

import dev.kigya.headway.auth.api.model.RefreshTokenResponse

interface RefreshTokenUseCaseContract {
    suspend operator fun invoke(
        refreshToken: String,
        fingerprint: String,
    ): RefreshTokenResponse
}
