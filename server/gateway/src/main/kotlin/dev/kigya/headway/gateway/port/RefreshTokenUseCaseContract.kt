package dev.kigya.headway.gateway.port

import dev.kigya.headway.gateway.model.RefreshTokenPayload

interface RefreshTokenUseCaseContract {
    suspend operator fun invoke(
        refreshToken: String,
        fingerprint: String,
    ): RefreshTokenPayload
}
