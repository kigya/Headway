package dev.kigya.headway.gateway.domain.repository

import java.util.UUID

internal interface LearningGuestSessionRevocationContract {
    suspend fun revokeGuestSession(sessionId: UUID)
}
