package dev.kigya.headway.api.schema

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dev.kigya.headway.api.model.AuthPayload
import dev.kigya.headway.api.model.DependencyHealth
import dev.kigya.headway.api.model.HealthPayload
import dev.kigya.headway.api.model.PublicUser
import dev.kigya.headway.api.model.RefreshTokenPayload
import dev.kigya.headway.api.model.ServiceStatus
import dev.kigya.headway.api.model.UserRole
import dev.kigya.headway.api.port.LoginWithGoogleUseCaseContract
import dev.kigya.headway.api.port.RefreshTokenUseCaseContract

internal fun SchemaBuilder.authSchema(
    loginWithGoogleUseCaseContract: LoginWithGoogleUseCaseContract,
    refreshTokenUseCaseContract: RefreshTokenUseCaseContract,
) {
    enum<UserRole>()
    type<PublicUser>()
    type<AuthPayload>()
    type<RefreshTokenPayload>()

    mutation("loginWithGoogle") {
        description = "Authorization via Google ID Token"
        resolver { idToken: String, fingerprint: String -> loginWithGoogleUseCaseContract(idToken, fingerprint) }
    }

    mutation("refreshToken") {
        description = "Refresh access token"
        resolver { refreshToken: String, fingerprint: String -> refreshTokenUseCaseContract(refreshToken, fingerprint) }
    }
}
