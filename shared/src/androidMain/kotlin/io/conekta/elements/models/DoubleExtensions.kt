package io.conekta.elements.models

actual fun Double.toFixed(decimals: Int): String = String.format("%.${decimals}f", this)
