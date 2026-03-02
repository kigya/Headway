package dev.kigya.headway.gateway.presentation.schema

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dev.kigya.headway.gateway.domain.usecase.LoginWithGoogleUseCase
import dev.kigya.headway.gateway.domain.usecase.RefreshAccessTokenUseCase
import dev.kigya.headway.gateway.model.GatewaySessionPlatform
import dev.kigya.headway.gateway.presentation.routes.GatewayGraphqlOperation

internal fun SchemaBuilder.authSchema(
    loginWithGoogle: LoginWithGoogleUseCase,
    refreshToken: RefreshAccessTokenUseCase,
) {
    mutation(GatewayGraphqlOperation.LoginWithGoogle.name) {
        description = "Authorization via Google ID Token"
        resolver { idToken: String, fingerprint: String, platform: GatewaySessionPlatform ->
            loginWithGoogle(
                idToken = idToken,
                fingerprint = fingerprint,
                platform = platform,
            )
        }
    }

    mutation(GatewayGraphqlOperation.RefreshToken.name) {
        description = "Refresh access token"
        resolver { refreshToken: String, fingerprint: String -> refreshToken(refreshToken, fingerprint) }
    }
}
