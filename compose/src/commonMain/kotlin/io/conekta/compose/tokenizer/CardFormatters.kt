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
     */
    fun formatCardNumber(value: TextFieldValue): TextFieldValue {
        val digits = value.text.filter { it.isDigit() }
        if (digits.length > 16) {
            return value
        }

        val formatted = digits.chunked(4).joinToString(" ")
        val cursorPosition = minOf(formatted.length, value.selection.start + (formatted.length - value.text.length))

        return TextFieldValue(
            text = formatted,
            selection = TextRange(cursorPosition),
        )
    }

    /**
     * Format expiry date as MM/YY
     * Returns TextFieldValue to preserve cursor position
     */
    fun formatExpiryDate(value: TextFieldValue): TextFieldValue {
        val digits = value.text.filter { it.isDigit() }
        if (digits.length > 4) {
            return value
        }

        val formatted =
            when {
                digits.isEmpty() -> ""
                digits.length <= 2 -> digits
                else -> "${digits.substring(0, 2)}/${digits.substring(2, minOf(4, digits.length))}"
            }

        val cursorPosition = minOf(formatted.length, value.selection.start + (formatted.length - value.text.length))

        return TextFieldValue(
            text = formatted,
            selection = TextRange(cursorPosition),
        )
    }

    /**
     * Format CVV (3-4 digits)
     * Returns TextFieldValue to preserve cursor position
     */
    fun formatCvv(
        value: TextFieldValue,
        brand: CardBrand,
    ): TextFieldValue {
        val maxLength = if (brand == CardBrand.AMEX) 4 else 3
        val digits = value.text.filter { it.isDigit() }

        if (digits.length > maxLength) {
            return value
        }

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
