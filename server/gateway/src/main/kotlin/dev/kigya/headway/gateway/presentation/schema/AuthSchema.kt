package dev.kigya.headway.gateway.presentation.schema

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dev.kigya.headway.gateway.model.GatewayGoogleLoginResponse
import dev.kigya.headway.gateway.model.PublicUser
import dev.kigya.headway.gateway.model.GatewayRefreshAccessTokenResponse
import dev.kigya.headway.gateway.model.UserRole
import dev.kigya.headway.gateway.domain.usecase.LoginWithGoogleUseCase
import dev.kigya.headway.gateway.domain.usecase.RefreshAccessTokenUseCase

internal fun SchemaBuilder.authSchema(
    loginWithGoogle: LoginWithGoogleUseCase,
    refreshToken: RefreshAccessTokenUseCase,
) {
    enum<UserRole>()
    type<PublicUser>()
    type<GatewayGoogleLoginResponse>()
    type<GatewayRefreshAccessTokenResponse>()

    mutation("loginWithGoogle") {
        description = "Authorization via Google ID Token"
        resolver { idToken: String, fingerprint: String -> loginWithGoogle(idToken, fingerprint) }
    }

    mutation("refreshToken") {
        description = "Refresh access token"
        resolver { refreshToken: String, fingerprint: String -> refreshToken(refreshToken, fingerprint) }
    }
}
