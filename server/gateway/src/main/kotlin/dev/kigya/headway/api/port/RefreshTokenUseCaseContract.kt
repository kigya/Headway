package dev.kigya.headway.api.port

import dev.kigya.headway.api.model.RefreshTokenPayload

interface RefreshTokenUseCaseContract {
    suspend operator fun invoke(
        refreshToken: String,
        fingerprint: String,
    ): RefreshTokenPayload
}
