package io.conekta.elements.utils

actual fun currentTwoDigitYear(): Int = (kotlin.js.Date().getFullYear() % 100).toInt()
