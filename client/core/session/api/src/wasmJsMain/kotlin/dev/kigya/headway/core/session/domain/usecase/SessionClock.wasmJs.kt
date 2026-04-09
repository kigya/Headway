package dev.kigya.headway.core.session.domain.usecase

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal actual fun readEpochMillis(): Long = Clock.System.now().toEpochMilliseconds()
