package io.conekta.elements.utils

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate

actual fun currentTwoDigitYear(): Int {
    val year = NSCalendar.currentCalendar.component(NSCalendarUnitYear, fromDate = NSDate())
    return (year % 100).toInt()
}

actual fun currentMonth(): Int = NSCalendar.currentCalendar.component(NSCalendarUnitMonth, fromDate = NSDate()).toInt()
