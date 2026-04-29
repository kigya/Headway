package dev.kigya.headway.core.session.model

data class HomeUserSummary(
    val displayName: String,
    val avatarUrl: String?,
    val dateLabel: String,
    val accessRole: HomeAccessRole,
    val roleLabel: String?,
    val readinessPercent: Int?,
    val nextSessionType: HomeNextSessionType?,
    val nextSessionTypeLabel: String?,
)
