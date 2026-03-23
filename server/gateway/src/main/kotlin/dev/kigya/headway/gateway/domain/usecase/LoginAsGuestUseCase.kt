package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.gateway.domain.repository.AuthRepositoryContract
import dev.kigya.headway.gateway.model.GatewayGuestLoginResponse

internal class LoginAsGuestUseCase(
    private val authRepository: AuthRepositoryContract,
) {
    suspend operator fun invoke(): GatewayGuestLoginResponse = authRepository.loginAsGuest()
}
