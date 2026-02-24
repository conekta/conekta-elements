package io.conekta.elements.validation

data class RequiredFieldValidationRule(
    val value: String,
    val requiredMessage: String,
)

object RequiredFieldValidator {
    fun firstError(vararg rules: RequiredFieldValidationRule): String? =
        rules.firstOrNull { it.value.isBlank() }?.requiredMessage
}
