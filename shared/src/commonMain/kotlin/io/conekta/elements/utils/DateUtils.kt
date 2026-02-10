package io.conekta.elements.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/**
 * Returns the current year as a two-digit integer (e.g. 26 for 2026)
 */
fun currentTwoDigitYear(): Int =
    Clock.System
        .now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .year % 100
