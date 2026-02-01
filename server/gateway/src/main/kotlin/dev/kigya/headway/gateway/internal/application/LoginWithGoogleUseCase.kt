package dev.kigya.headway.gateway.internal.application

import dev.kigya.headway.gateway.api.model.AuthPayload
import dev.kigya.headway.gateway.api.port.LoginWithGoogleUseCaseContract
import dev.kigya.headway.gateway.internal.client.AuthServiceClientContract
import dev.kigya.headway.gateway.internal.mapping.toPublic

internal class LoginWithGoogleUseCase(
    private val authClient: AuthServiceClientContract,
) : LoginWithGoogleUseCaseContract {
    override suspend fun invoke(idToken: String, fingerprint: String): AuthPayload {
        val dto = authClient.loginWithGoogle(idToken = idToken, fingerprint = fingerprint)
        return dto.toPublic()
    }
}
