package io.conekta.elements.tokenizer.validators

import io.conekta.elements.tokenizer.models.CardBrand

data class ValidationMessages(
    val required: String,
    val cardMinLength: String,
    val expiryYearInvalid: String,
    val cvvMinLength: String,
)

data class FieldError(
    val isError: Boolean = false,
    val message: String? = null,
)

data class ValidationResult(
    val cardholderName: FieldError = FieldError(),
    val cardNumber: FieldError = FieldError(),
    val expiryDate: FieldError = FieldError(),
    val cvv: FieldError = FieldError(),
) {
    val hasError: Boolean
        get() = cardholderName.isError || cardNumber.isError || expiryDate.isError || cvv.isError
}

/**
 * Validate the full card form.
 * Pure logic — no Compose dependencies.
 */
fun validateForm(
    cardholderName: String,
    cardNumber: String,
    expiryDate: String,
    cvv: String,
    detectedBrand: CardBrand,
    collectCardholderName: Boolean,
    messages: ValidationMessages,
): ValidationResult {
    val cardDigits = cardNumber.filter { it.isDigit() }

    return ValidationResult(
        cardholderName = validateRequired(cardholderName, collectCardholderName, messages.required),
        cardNumber = validateCardNumber(cardDigits, cardNumber, messages),
        expiryDate = validateExpiry(expiryDate, messages),
        cvv = validateCvv(cvv, detectedBrand, messages),
    )
}

private fun validateRequired(
    value: String,
    shouldValidate: Boolean,
    requiredMsg: String,
): FieldError = if (shouldValidate && value.isBlank()) FieldError(true, requiredMsg) else FieldError()

private fun validateCardNumber(
    digits: String,
    rawText: String,
    messages: ValidationMessages,
): FieldError =
    when {
        rawText.isBlank() -> FieldError(true, messages.required)
        !isValidCardNumber(digits) -> FieldError(true, messages.cardMinLength)
        else -> FieldError()
    }

private fun validateExpiry(
    expiryDate: String,
    messages: ValidationMessages,
): FieldError {
    val digits = expiryDate.filter { it.isDigit() }
    if (expiryDate.isBlank()) return FieldError(true, messages.required)
    if (digits.length != 4) return FieldError(true, messages.expiryYearInvalid)

    val month = digits.substring(0, 2)
    val year = digits.substring(2, 4)

    return if (!isValidExpiryDate(month, year)) {
        FieldError(true, messages.expiryYearInvalid)
    } else {
        FieldError()
    }
}

private fun validateCvv(
    cvv: String,
    brand: CardBrand,
    messages: ValidationMessages,
): FieldError =
    when {
        cvv.isBlank() -> FieldError(true, messages.required)
        !isValidCvv(cvv) -> FieldError(true, messages.cvvMinLength)
        else -> FieldError()
    }
