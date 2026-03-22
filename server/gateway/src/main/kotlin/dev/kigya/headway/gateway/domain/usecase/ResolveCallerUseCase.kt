package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.domain.repository.AuthRepositoryContract
import dev.kigya.headway.gateway.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.gateway.model.GatewayUser

internal class ResolveCallerUseCase(
    private val authRepository: AuthRepositoryContract,
    private val databaseRepository: DatabaseRepositoryContract,
) {
    suspend operator fun invoke(authorizationHeader: String?): GatewayUser {
        val bearerToken = authorizationHeader
            ?.trim()
            ?.takeIf { it.startsWith(BEARER_PREFIX) }
            ?.removePrefix(BEARER_PREFIX)
            ?.trim()
            ?.takeIf(String::isNotBlank)
            ?: throw GatewayException.Unauthorized("Unauthorized")

        val authResponse = authRepository.validateToken(bearerToken)
        if (!authResponse.isValid) {
            throw GatewayException.Unauthorized("Unauthorized")
        }

        return databaseRepository.getUserById(authResponse.userUuid)
    }

    private companion object {
        const val BEARER_PREFIX = "Bearer "
    }
}
