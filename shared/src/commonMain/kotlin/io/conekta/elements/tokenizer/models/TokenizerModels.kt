package io.conekta.elements.tokenizer.models

/**
 * Configuration for the Conekta Tokenizer
 */
data class TokenizerConfig(
    val publicKey: String,
    val merchantName: String = "Demo Store",
    val collectCardholderName: Boolean = true
)

/**
 * Result of a successful tokenization
 */
data class TokenResult(
    val token: String,
    val cardBrand: String,
    val lastFour: String
)

/**
 * Error during tokenization
 */
sealed class TokenizerError {
    data class ValidationError(val message: String) : TokenizerError()
    data class NetworkError(val message: String) : TokenizerError()
    data class ApiError(val code: String, val message: String) : TokenizerError()
}

/**
 * Card brand types
 */
enum class CardBrand {
    VISA,
    MASTERCARD,
    AMEX,
    UNKNOWN
}

