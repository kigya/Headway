@file:Suppress("LongParameterList")

package dev.kigya.headway.gateway.presentation

import dev.kigya.headway.common.util.Environment
import dev.kigya.headway.gateway.domain.usecase.CheckHealthStatusUseCase
import dev.kigya.headway.gateway.domain.usecase.GetHomeScreenUseCase
import dev.kigya.headway.gateway.domain.usecase.InviteUserUseCase
import dev.kigya.headway.gateway.domain.usecase.LoginAsGuestUseCase
import dev.kigya.headway.gateway.domain.usecase.LoginWithGoogleUseCase
import dev.kigya.headway.gateway.domain.usecase.RefreshAccessTokenUseCase
import dev.kigya.headway.gateway.domain.usecase.ResolvePrincipalUseCase

internal data class GatewayApiBindings(
    val environment: Environment,
    val checkHealthStatus: CheckHealthStatusUseCase,
    val loginWithGoogle: LoginWithGoogleUseCase,
    val loginAsGuest: LoginAsGuestUseCase,
    val refreshToken: RefreshAccessTokenUseCase,
    val inviteUser: InviteUserUseCase,
    val resolvePrincipal: ResolvePrincipalUseCase,
    val getHomeScreen: GetHomeScreenUseCase,
)
