package dev.kigya.headway.core.session.domain.usecase

import platform.Foundation.NSDate

internal actual fun readEpochMillis(): Long {
    val secondsSinceUnixEpoch =
        NSDate().timeIntervalSinceReferenceDate + SECONDS_FROM_UNIX_EPOCH_TO_NS_DATE_REFERENCE
    return (secondsSinceUnixEpoch * MILLIS_PER_SECOND).toLong()
}

private const val MILLIS_PER_SECOND: Double = 1_000.0

private const val SECONDS_FROM_UNIX_EPOCH_TO_NS_DATE_REFERENCE: Double = 978_307_200.0
