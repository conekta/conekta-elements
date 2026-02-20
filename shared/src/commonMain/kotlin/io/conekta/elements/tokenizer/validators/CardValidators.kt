package io.conekta.elements.tokenizer.validators

/**
 * Validation error messages
 *
 * Note: Default values are in English for backward compatibility.
 * For internationalization, provide localized strings from your i18n system.
 *
 * @see io.conekta.compose.i18n.Strings for i18n integration
 */
data class ValidationErrorMessages(
    val required: String,
    val invalidCard: String,
    val expiredCard: String,
    val invalidCvv: String,
    val onlyDigits: String,
)

/**
 * Card number validation using Luhn algorithm
 */
fun isValidCardNumber(cardNumber: String): Boolean {
    val digits = cardNumber.filter { it.isDigit() }
    if (digits.length < 13 || digits.length > 19) return false

    return luhnCheck(digits)
}

/**
 * Luhn algorithm implementation
 */
private fun luhnCheck(cardNumber: String): Boolean {
    var sum = 0
    var alternate = false

    for (i in cardNumber.length - 1 downTo 0) {
        var digit = cardNumber[i].toString().toInt()

        if (alternate) {
            digit *= 2
            if (digit > 9) {
                digit -= 9
            }
        }

        sum += digit
        alternate = !alternate
    }

    return sum % 10 == 0
}

/**
 * Validate expiry date
 * Note: Actual implementation should use kotlinx-datetime for multiplatform support
 * For now, we'll do basic validation
 */
fun isValidExpiryDate(
    month: String,
    year: String,
): Boolean {
    val monthInt = month.toIntOrNull() ?: return false
    val yearInt = year.toIntOrNull() ?: return false

    if (monthInt !in 1..12) return false
    if (year.length != 2) return false

    // TODO: Add actual date comparison using kotlinx-datetime
    // For now, just validate format
    return true
}

/**
 * Validate CVV (3-4 digits accepted)
 */
fun isValidCvv(cvv: String): Boolean {
    val digits = cvv.filter { it.isDigit() }
    return digits.length in 3..4
}
