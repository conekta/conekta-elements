package io.conekta.elements.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents a monetary amount as handled by Conekta API.
 *
 * In Conekta API, amounts are expressed as integer values in cents (the smallest unit).
 * For example, an amount of 260.70 is represented as 26070 in the API.
 *
 * @property value The amount in cents as an integer. Example: 26070 represents 260.70
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
class Amount(
    private val value: Int,
) {
    /**
     * Converts the amount from cents (integer value) to major units (decimal value).
     *
     * Conekta API stores amounts as integers in cents to avoid precision issues
     * with decimals. This method converts that value to the major unit by dividing by 100.
     *
     * @return The amount in major units. Example: 26070 -> 260.70
     */
    fun apiFormat(): Double = value / API_DIVISOR

    /**
     * Formats the amount with a fixed number of decimal places.
     *
     * @param decimals Number of decimal places to display
     * @return Formatted string. Example: toFixed(2) -> "260.70"
     */
    fun apiFormatToFixed(decimals: Int): String = apiFormat().toFixed(decimals)

    fun toFixed(decimals: Int): String = apiFormatToFixed(decimals)

    override fun toString() = value.toString()

    companion object {
        private const val API_DIVISOR = 100.0
    }
}
