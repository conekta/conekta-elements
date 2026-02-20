package io.conekta.elements.utils

import java.util.Calendar

actual fun currentTwoDigitYear(): Int = Calendar.getInstance()[Calendar.YEAR] % 100
