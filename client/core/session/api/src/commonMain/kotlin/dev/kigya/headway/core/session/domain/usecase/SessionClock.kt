package dev.kigya.headway.core.session.domain.usecase

fun interface SessionClock {
    fun nowEpochMillis(): Long
}

internal expect fun readEpochMillis(): Long

fun systemSessionClock(): SessionClock = SessionClock { readEpochMillis() }
