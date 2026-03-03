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

private const val MEXICO_CITY_UTC_OFFSET_SECONDS = -6 * 60 * 60
private const val SECONDS_PER_DAY = 24 * 60 * 60

/**
 * Converts an epoch seconds timestamp into Mexico City calendar date and 12-hour time.
 *
 * The output matches checkout success copy requirements:
 * - date: dd/MM/yyyy
 * - time: hh:mm am/pm
 */
fun formatEpochSecondsInMexicoCity(epochSeconds: Long): Pair<String, String> {
    val mexicoEpoch = epochSeconds + MEXICO_CITY_UTC_OFFSET_SECONDS
    val daysSinceUnixEpoch = floorDiv(mexicoEpoch, SECONDS_PER_DAY.toLong())
    val secondsOfDay = floorMod(mexicoEpoch, SECONDS_PER_DAY.toLong()).toInt()

    val (year, month, day) = civilFromUnixDays(daysSinceUnixEpoch)
    val hour24 = secondsOfDay / 3600
    val minute = (secondsOfDay % 3600) / 60
    val hour12 = ((hour24 + 11) % 12) + 1
    val period = if (hour24 >= 12) "pm" else "am"

    val date = "${day.toString().padStart(2, '0')}/${month.toString().padStart(2, '0')}/$year"
    val time = "${hour12.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} $period"
    return date to time
}

private fun floorDiv(
    x: Long,
    y: Long,
): Long = if (x >= 0L) x / y else -((-x + y - 1) / y)

private fun floorMod(
    x: Long,
    y: Long,
): Long = x - floorDiv(x, y) * y

private fun civilFromUnixDays(daysSinceUnixEpoch: Long): Triple<Int, Int, Int> {
    val z = daysSinceUnixEpoch + 719468
    val era = if (z >= 0) z / 146097 else (z - 146096) / 146097
    val doe = z - era * 146097
    val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
    var year = (yoe + era * 400).toInt()
    val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
    val mp = (5 * doy + 2) / 153
    val day = (doy - (153 * mp + 2) / 5 + 1).toInt()
    val month = (mp + if (mp < 10) 3 else -9).toInt()
    if (month <= 2) year += 1
    return Triple(year, month, day)
}
