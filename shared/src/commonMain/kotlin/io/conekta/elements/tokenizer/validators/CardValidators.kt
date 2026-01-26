package io.conekta.elements.tokenizer.validators

/**
 * Validation error messages
 */
data class ValidationErrorMessages(
    val required: String = "This field is required",
    val invalidCard: String = "Invalid card number",
    val expiredCard: String = "Card expired",
    val invalidCvv: String = "Invalid CVV",
    val onlyDigits: String = "Only digits allowed"
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
fun isValidExpiryDate(month: String, year: String): Boolean {
    val monthInt = month.toIntOrNull() ?: return false
    val yearInt = year.toIntOrNull() ?: return false
    
    if (monthInt !in 1..12) return false
    if (year.length != 2) return false
    
    // TODO: Add actual date comparison using kotlinx-datetime
    // For now, just validate format
    return true
}

/**
 * Validate CVV
 */
fun isValidCvv(cvv: String, cardBrand: String = ""): Boolean {
    val digits = cvv.filter { it.isDigit() }
    return when (cardBrand.uppercase()) {
        "AMEX" -> digits.length == 4
        else -> digits.length == 3
    }
}

