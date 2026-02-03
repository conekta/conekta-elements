package io.conekta.compose.i18n

/**
 * Language enum for supported languages
 */
enum class Language(
    val code: String,
) {
    ES("es"),
    EN("en"),
}

/**
 * String resources data class for internationalization
 */
data class Strings(
    // Validation error messages
    val validationRequired: String,
    val validationInvalidCard: String,
    val validationExpiredCard: String,
    val validationInvalidCvv: String,
    val validationOnlyDigits: String,
    // Content descriptions for accessibility
    val contentDescriptionConektaLogo: String,
    val contentDescriptionVisaCard: String,
    val contentDescriptionMastercardCard: String,
    val contentDescriptionAmexCard: String,
    val contentDescriptionCardBrand: String,
)
