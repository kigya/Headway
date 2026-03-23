package dev.kigya.headway.auth.internal.domain.repository

import java.util.UUID

internal data class GuestAccessTokenPayload(
    val sessionId: UUID,
    val scopes: List<String>,
)
