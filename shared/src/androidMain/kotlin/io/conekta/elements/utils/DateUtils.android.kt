package io.conekta.elements.utils

import java.util.Calendar

actual fun currentTwoDigitYear(): Int = Calendar.getInstance().get(Calendar.YEAR) % 100
