package io.conekta.compose.tokenizer

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.conekta.elements.tokenizer.formatters.CardInputFormatters
import io.conekta.elements.tokenizer.models.CardBrand
import io.conekta.elements.tokenizer.validators.isValidCardNumber as sharedIsValidCardNumber
import io.conekta.elements.tokenizer.validators.isValidCvv as sharedIsValidCvv
import io.conekta.elements.tokenizer.validators.isValidExpiryDate as sharedIsValidExpiryDate

/**
 * Compose-specific wrappers around [CardInputFormatters].
 * Handles [TextFieldValue] cursor positioning; pure logic lives in shared.
 */
object CardFormatters {
    fun formatCardNumber(value: TextFieldValue): TextFieldValue {
        val formatted = CardInputFormatters.formatCardNumber(value.text)
        return withCursor(formatted, value)
    }

    fun formatExpiryDate(value: TextFieldValue): TextFieldValue {
        val formatted = CardInputFormatters.formatExpiryDate(value.text)
        return withCursor(formatted, value)
    }

    fun formatCvv(
        value: TextFieldValue,
        brand: CardBrand,
    ): TextFieldValue {
        val formatted = CardInputFormatters.formatCvv(value.text)
        return TextFieldValue(
            text = formatted,
            selection = TextRange(formatted.length),
        )
    }

    fun detectCardBrand(cardNumber: String): CardBrand =
        CardInputFormatters.detectCardBrand(cardNumber)

    fun isValidCardNumber(cardNumber: String): Boolean = sharedIsValidCardNumber(cardNumber)

    fun isValidExpiryDate(expiryDate: String): Boolean {
        val digits = expiryDate.filter { it.isDigit() }
        if (digits.length != 4) return false

        val month = digits.substring(0, 2)
        val year = digits.substring(2, 4)

        return sharedIsValidExpiryDate(month, year)
    }

    fun isValidCvv(
        cvv: String,
        brand: CardBrand,
    ): Boolean = sharedIsValidCvv(cvv, brand.name)

    private fun withCursor(formatted: String, original: TextFieldValue): TextFieldValue {
        val cursorPosition = minOf(
            formatted.length,
            original.selection.start + (formatted.length - original.text.length),
        )
        return TextFieldValue(
            text = formatted,
            selection = TextRange(cursorPosition),
        )
    }
}
