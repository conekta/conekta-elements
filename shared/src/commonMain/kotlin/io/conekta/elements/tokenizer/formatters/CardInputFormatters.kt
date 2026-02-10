package io.conekta.elements.tokenizer.formatters

import io.conekta.elements.tokenizer.models.CardBrand
import io.conekta.elements.utils.currentTwoDigitYear

/**
 * Pure formatting logic for card input fields.
 * No Compose dependencies — can be used from any platform.
 */
object CardInputFormatters {
    /**
     * Format card number string with spaces every 4 digits.
     * Limits input to 16 digits.
     */
    fun formatCardNumber(text: String): String {
        val digits = text.filter { it.isDigit() }.take(16)
        return digits.chunked(4).joinToString(" ")
    }

    /**
     * Format expiry date string as MM/YY.
     * Validates month <= 12, prevents years < current year.
     */
    fun formatExpiryDate(text: String): String {
        val digits = text.filter { it.isDigit() }.take(4)

        return when {
            digits.isEmpty() -> ""
            digits.length == 1 -> formatMonthFirstDigit(digits)
            digits.length >= 2 -> formatMonthAndYear(digits)
            else -> digits
        }
    }

    /**
     * Format CVV string (3-4 digits only).
     */
    fun formatCvv(text: String): String = text.filter { it.isDigit() }.take(4)

    /**
     * Detect card brand from card number digits.
     */
    fun detectCardBrand(cardNumber: String): CardBrand {
        val digits = cardNumber.filter { it.isDigit() }

        return when {
            digits.isEmpty() -> CardBrand.UNKNOWN
            digits.startsWith("4") -> CardBrand.VISA
            digits.startsWith("5") || digits.startsWith("2") -> CardBrand.MASTERCARD
            digits.startsWith("34") || digits.startsWith("37") -> CardBrand.AMEX
            else -> CardBrand.UNKNOWN
        }
    }

    internal fun formatMonthFirstDigit(digits: String): String = if (digits[0].digitToInt() > 1) "0$digits" else digits

    internal fun formatMonthAndYear(digits: String): String {
        val monthStr = digits.substring(0, 2)
        val month = monthStr.toIntOrNull() ?: 0

        if (month > 12) return digits.substring(0, 1)
        if (digits.length <= 2) return monthStr

        val yearPart = formatYearPart(digits.substring(2))
        return if (yearPart.isEmpty()) monthStr else "$monthStr/$yearPart"
    }

    internal fun formatYearPart(yearDigits: String): String {
        val minYear = currentTwoDigitYear()
        val minFirstDigit = minYear / 10

        val firstDigit = yearDigits[0].digitToInt()
        if (firstDigit < minFirstDigit) return ""

        if (yearDigits.length < 2) return yearDigits

        val year = yearDigits.substring(0, 2).toInt()
        return if (year < minYear) yearDigits.substring(0, 1) else yearDigits.substring(0, 2)
    }
}
