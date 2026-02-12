package dev.kigya.headway.gateway.presentation.schema

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dev.kigya.headway.gateway.domain.usecase.LoginWithGoogleUseCase
import dev.kigya.headway.gateway.domain.usecase.RefreshAccessTokenUseCase
import dev.kigya.headway.gateway.model.GatewayGoogleLoginResponse
import dev.kigya.headway.gateway.model.GatewayRefreshAccessTokenResponse
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.model.GatewayUserRole
import dev.kigya.headway.gateway.presentation.routes.GatewayGraphqlOperation

internal fun SchemaBuilder.authSchema(
    loginWithGoogle: LoginWithGoogleUseCase,
    refreshToken: RefreshAccessTokenUseCase,
) {
    enum<GatewayUserRole>()
    type<GatewayUser>()
    type<GatewayGoogleLoginResponse>()
    type<GatewayRefreshAccessTokenResponse>()

    mutation(GatewayGraphqlOperation.LoginWithGoogle.name) {
        description = "Authorization via Google ID Token"
        resolver { idToken: String, fingerprint: String -> loginWithGoogle(idToken, fingerprint) }
    }

    mutation(GatewayGraphqlOperation.RefreshToken.name) {
        description = "Refresh access token"
        resolver { refreshToken: String, fingerprint: String -> refreshToken(refreshToken, fingerprint) }
    }
}
