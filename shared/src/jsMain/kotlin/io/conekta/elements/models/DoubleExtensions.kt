package io.conekta.elements.models

actual fun Double.toFixed(decimals: Int): String = this.asDynamic().toFixed(decimals)
