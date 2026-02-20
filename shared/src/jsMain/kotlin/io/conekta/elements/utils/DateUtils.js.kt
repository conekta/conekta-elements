package io.conekta.elements.utils

import kotlin.js.Date

actual fun currentTwoDigitYear(): Int = Date().getFullYear() % 100
