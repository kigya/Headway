package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.gateway.model.RefreshTokenPayload
import dev.kigya.headway.gateway.port.RefreshTokenUseCaseContract
import dev.kigya.headway.gateway.internal.client.AuthServiceClientContract

internal class RefreshTokenUseCase(
    private val authClient: AuthServiceClientContract,
) : RefreshTokenUseCaseContract {
    override suspend fun invoke(refreshToken: String, fingerprint: String): RefreshTokenPayload {
        val dto = authClient.refreshToken(refreshToken = refreshToken, fingerprint = fingerprint)
        return RefreshTokenPayload(accessToken = dto.accessToken)
    }
}
