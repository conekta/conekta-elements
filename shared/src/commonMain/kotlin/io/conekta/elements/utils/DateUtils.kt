package io.conekta.elements.utils

/**
 * Returns the current calendar year as two digits (00-99), using the device/system clock.
 *
 * Example: 2026 -> 26.
 */
expect fun currentTwoDigitYear(): Int

/**
 * Returns the current calendar month in 1-based format (1..12), using the device/system clock.
 *
 * Values map to January=1 through December=12.
 */
expect fun currentMonth(): Int
