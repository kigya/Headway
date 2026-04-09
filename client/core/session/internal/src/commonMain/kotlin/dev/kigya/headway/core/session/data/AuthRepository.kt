package dev.kigya.headway.core.session.data

import com.apollographql.apollo.api.Optional
import dev.kigya.headway.core.apollo.generated.LoginAsGuestMutation
import dev.kigya.headway.core.apollo.generated.LoginWithGoogleMutation
import dev.kigya.headway.core.apollo.generated.LogoutGuestMutation
import dev.kigya.headway.core.apollo.generated.RefreshTokenMutation
import dev.kigya.headway.core.apollo.generated.type.GatewaySessionPlatform
import dev.kigya.headway.core.network.api.HeadwayGraphqlOperationExecutor
import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.outcome.mapFailure
import dev.kigya.headway.core.outcome.mapSuccess
import dev.kigya.headway.core.session.domain.HeadwaySessionGatewayPlatform
import dev.kigya.headway.core.session.domain.SessionRuntimeIdentityContract
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import dev.kigya.headway.core.session.domain.error.mapHeadwayGraphqlFailureToSessionError
import dev.kigya.headway.core.session.domain.repository.AuthRepositoryContract
import dev.kigya.headway.core.session.model.LocalSessionRecord

internal class AuthRepository(
    private val executor: HeadwayGraphqlOperationExecutor,
    private val runtimeIdentity: SessionRuntimeIdentityContract,
) : AuthRepositoryContract {

    override suspend fun loginWithGoogle(
        idToken: String,
    ): Outcome<SessionDomainError, LocalSessionRecord.Registered> {
        val mutation = LoginWithGoogleMutation(
            idToken = idToken,
            fingerprint = runtimeIdentity.deviceFingerprint,
            platform = runtimeIdentity.sessionGatewayPlatform.toGatewaySessionPlatform(),
        )
        return executor.executeMutation(mutation)
            .mapFailure(::mapHeadwayGraphqlFailureToSessionError)
            .mapSuccess { data ->
                val payload = data.loginWithGoogle
                LocalSessionRecord.Registered(
                    accessToken = payload.accessToken,
                    refreshToken = payload.refreshToken,
                    userId = payload.user.id.toString(),
                    userEmail = payload.user.email,
                    userName = payload.user.name,
                )
            }
    }

    override suspend fun loginAsGuest(): Outcome<SessionDomainError, LocalSessionRecord.Guest> {
        val mutation = LoginAsGuestMutation(stub = Optional.Absent)
        return executor.executeMutation(mutation).mapFailure(::mapHeadwayGraphqlFailureToSessionError)
            .mapSuccess { data ->
                val payload = data.loginAsGuest
                LocalSessionRecord.Guest(
                    accessToken = payload.accessToken,
                    expiresAtEpochMs = payload.expiresAtEpochMs,
                )
            }
    }

    override suspend fun refreshRegisteredAccess(
        refreshToken: String,
    ): Outcome<SessionDomainError, String> {
        val mutation = RefreshTokenMutation(
            refreshToken = refreshToken,
            fingerprint = runtimeIdentity.deviceFingerprint,
        )
        return executor.executeMutation(mutation).mapFailure(::mapHeadwayGraphqlFailureToSessionError)
            .mapSuccess { it.refreshToken.accessToken }
    }

    override suspend fun revokeGuestSession(): Outcome<SessionDomainError, Unit> =
        executor.executeMutation(LogoutGuestMutation()).mapFailure(::mapHeadwayGraphqlFailureToSessionError)
            .mapSuccess { }
}

private fun HeadwaySessionGatewayPlatform.toGatewaySessionPlatform(): GatewaySessionPlatform =
    when (this) {
    HeadwaySessionGatewayPlatform.Android -> GatewaySessionPlatform.ANDROID
    HeadwaySessionGatewayPlatform.Ios -> GatewaySessionPlatform.IOS
    HeadwaySessionGatewayPlatform.Desktop -> GatewaySessionPlatform.DESKTOP
    HeadwaySessionGatewayPlatform.Web -> GatewaySessionPlatform.WEB
}
