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

    /**
     * Format expiry date as MM/YY
     * Returns TextFieldValue to preserve cursor position
     * Limits input to 4 digits (MMYY) and validates month <= 12
     * Prevents entering years < 26
     */
    fun formatExpiryDate(value: TextFieldValue): TextFieldValue {
        val digits = value.text.filter { it.isDigit() }.take(4)

        val formatted =
            when {
                digits.isEmpty() -> ""
                digits.length == 1 -> {
                    // If first digit > 1, auto-add 0 prefix (e.g., "2" -> "02")
                    if (digits[0].digitToInt() > 1) {
                        "0$digits"
                    } else {
                        digits
                    }
                }
                digits.length >= 2 -> {
                    val monthStr = digits.substring(0, 2)
                    val month = monthStr.toIntOrNull() ?: 0

                    // If month > 12, keep only first digit
                    if (month > 12) {
                        digits.substring(0, 1)
                    } else {
                        // Process year part if present
                        if (digits.length > 2) {
                            val firstYearDigit = digits[2].digitToInt()

                            // Year must be >= 26, so first digit must be >= 2
                            if (firstYearDigit < 2) {
                                // Don't allow years starting with 0 or 1
                                "$monthStr"
                            } else if (digits.length == 4) {
                                val secondYearDigit = digits[3].digitToInt()
                                // If first digit is 2, second digit must be >= 6
                                if (firstYearDigit == 2 && secondYearDigit < 6) {
                                    "$monthStr/${digits[2]}"
                                } else {
                                    "$monthStr/${digits.substring(2, 4)}"
                                }
                            } else {
                                "$monthStr/${digits[2]}"
                            }
                        } else {
                            monthStr
                        }
                    }
                }
                else -> digits
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
