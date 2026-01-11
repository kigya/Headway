package dev.kigya.headway.internal.application

import dev.kigya.headway.api.model.RefreshTokenPayload
import dev.kigya.headway.api.port.RefreshTokenUseCaseContract
import dev.kigya.headway.internal.client.AuthServiceClientContract

internal class RefreshTokenUseCase(
    private val authClient: AuthServiceClientContract,
) : RefreshTokenUseCaseContract {
    override suspend fun invoke(refreshToken: String, fingerprint: String): RefreshTokenPayload {
        val dto = authClient.refreshToken(refreshToken = refreshToken, fingerprint = fingerprint)
        return RefreshTokenPayload(accessToken = dto.accessToken)
    }
}
