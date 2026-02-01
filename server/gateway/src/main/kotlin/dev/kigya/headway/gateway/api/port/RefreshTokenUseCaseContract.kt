package dev.kigya.headway.gateway.api.port

import dev.kigya.headway.gateway.api.model.RefreshTokenPayload

interface RefreshTokenUseCaseContract {
    suspend operator fun invoke(
        refreshToken: String,
        fingerprint: String,
    ): RefreshTokenPayload
}
