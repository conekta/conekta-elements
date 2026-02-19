package io.conekta.elements.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Returns the current year as a two-digit integer (e.g. 26 for 2026)
 *
 * Note: @OptIn required because kotlin.time.Clock is @ExperimentalTime in Kotlin 2.1.x.
 * It was stabilized in Kotlin 2.2.0.
 */
@OptIn(ExperimentalTime::class)
fun currentTwoDigitYear(): Int =
    Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .year % 100
