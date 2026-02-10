package io.conekta.elements.utils

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate

actual fun currentTwoDigitYear(): Int {
    val calendar = NSCalendar.currentCalendar
    val year = calendar.component(NSCalendarUnitYear, fromDate = NSDate())
    return (year % 100).toInt()
}
