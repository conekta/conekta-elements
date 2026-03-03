package io.conekta.elements.utils

import java.util.Calendar

actual fun currentTwoDigitYear(): Int = Calendar.getInstance()[Calendar.YEAR] % 100

actual fun currentMonth(): Int = Calendar.getInstance()[Calendar.MONTH] + 1
