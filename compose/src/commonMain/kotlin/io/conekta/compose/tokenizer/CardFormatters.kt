package io.conekta.compose.tokenizer

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.conekta.elements.tokenizer.models.CardBrand
import io.conekta.elements.tokenizer.validators.isValidCardNumber as sharedIsValidCardNumber
import io.conekta.elements.tokenizer.validators.isValidCvv as sharedIsValidCvv
import io.conekta.elements.tokenizer.validators.isValidExpiryDate as sharedIsValidExpiryDate

/**
 * Utility functions for formatting card input fields
 */
object CardFormatters {
    /**
     * Format card number with spaces every 4 digits
     * Returns TextFieldValue to preserve cursor position
     * Limits input to 16 digits
     */
    fun formatCardNumber(value: TextFieldValue): TextFieldValue {
        val digits = value.text.filter { it.isDigit() }.take(16)

        val formatted = digits.chunked(4).joinToString(" ")
        val cursorPosition = minOf(formatted.length, value.selection.start + (formatted.length - value.text.length))

        return TextFieldValue(
            text = formatted,
            selection = TextRange(cursorPosition),
        )
    }

    private const val MIN_YEAR = 26

    /**
     * Format expiry date as MM/YY
     * Returns TextFieldValue to preserve cursor position
     * Limits input to 4 digits (MMYY) and validates month <= 12
     * Prevents entering years < [MIN_YEAR]
     */
    fun formatExpiryDate(value: TextFieldValue): TextFieldValue {
        val digits = value.text.filter { it.isDigit() }.take(4)

        val formatted =
            when {
                digits.isEmpty() -> ""
                digits.length == 1 -> formatMonthFirstDigit(digits)
                digits.length >= 2 -> formatMonthAndYear(digits)
                else -> digits
            }

        val cursorPosition = minOf(formatted.length, value.selection.start + (formatted.length - value.text.length))

        return TextFieldValue(
            text = formatted,
            selection = TextRange(cursorPosition),
        )
    }

    private fun formatMonthFirstDigit(digits: String): String = if (digits[0].digitToInt() > 1) "0$digits" else digits

    private fun formatMonthAndYear(digits: String): String {
        val monthStr = digits.substring(0, 2)
        val month = monthStr.toIntOrNull() ?: 0

        if (month > 12) return digits.substring(0, 1)
        if (digits.length <= 2) return monthStr

        val yearPart = formatYearPart(digits.substring(2))
        return if (yearPart.isEmpty()) monthStr else "$monthStr/$yearPart"
    }

    private fun formatYearPart(yearDigits: String): String {
        val firstDigit = yearDigits[0].digitToInt()
        if (firstDigit < 2) return ""

        if (yearDigits.length < 2) return yearDigits

        val year = yearDigits.substring(0, 2).toInt()
        return if (year < MIN_YEAR) yearDigits.substring(0, 1) else yearDigits.substring(0, 2)
    }

    /**
     * Format CVV (3-4 digits)
     * Returns TextFieldValue to preserve cursor position
     * Limits input to 4 digits maximum
     */
    fun formatCvv(
        value: TextFieldValue,
        brand: CardBrand,
    ): TextFieldValue {
        val maxLength = 4 // Allow up to 4 digits for all cards
        val digits = value.text.filter { it.isDigit() }.take(maxLength)

        return TextFieldValue(
            text = digits,
            selection = TextRange(digits.length),
        )
    }

    /**
     * Detect card brand from card number
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

    /**
     * Validate card number using Luhn algorithm
     * Delegates to shared validation logic
     */
    fun isValidCardNumber(cardNumber: String): Boolean = sharedIsValidCardNumber(cardNumber)

    /**
     * Validate expiry date (MM/YY format)
     * Delegates to shared validation logic
     */
    fun isValidExpiryDate(expiryDate: String): Boolean {
        val digits = expiryDate.filter { it.isDigit() }
        if (digits.length != 4) return false

        val month = digits.substring(0, 2)
        val year = digits.substring(2, 4)

        return sharedIsValidExpiryDate(month, year)
    }

    /**
     * Validate CVV
     * Delegates to shared validation logic
     */
    fun isValidCvv(
        cvv: String,
        brand: CardBrand,
    ): Boolean = sharedIsValidCvv(cvv, brand.name)
}
