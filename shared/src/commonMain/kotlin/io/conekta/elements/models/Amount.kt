package io.conekta.elements.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
class Amount(
    private val value: Long,
) {
    fun apiFormat(): Double = value / API_DIVISOR

    fun toFixed(decimals: Int): String = apiFormat().toFixed(decimals)

    override fun toString() = value.toString()

    companion object {
        private const val API_DIVISOR = 100.0
    }
}
