package io.conekta.elements.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.math.pow
import kotlin.math.round

@OptIn(ExperimentalJsExport::class)
@JsExport
class Amount(
    private val value: Double,
) {
    fun apiFormat(): Double = value / 100.0

    fun toFixed(decimals: Int): String {
        val multiplier = 10.0.pow(decimals)
        val rounded = round(value * multiplier) / multiplier
        return rounded.toString().let { str ->
            val parts = str.split('.')
            val intPart = parts[0]
            val decPart = parts.getOrNull(1) ?: ""

            if (decimals == 0) {
                intPart
            } else {
                "$intPart.${decPart.padEnd(decimals, '0').take(decimals)}"
            }
        }
    }

    override fun toString() = value.toString()
}
