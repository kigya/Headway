package dev.kigya.headway.internal.application

import dev.kigya.headway.api.model.AuthPayload
import dev.kigya.headway.api.port.LoginWithGoogleUseCaseContract
import dev.kigya.headway.internal.client.AuthServiceClientContract
import dev.kigya.headway.internal.mapping.toPublic

internal class LoginWithGoogleUseCase(
    private val authClient: AuthServiceClientContract,
) : LoginWithGoogleUseCaseContract {
    override suspend fun invoke(idToken: String, fingerprint: String): AuthPayload {
        val dto = authClient.loginWithGoogle(idToken = idToken, fingerprint = fingerprint)
        return dto.toPublic()
    }
}
