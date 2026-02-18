package io.conekta.elements.tokenizer.models

/**
 * Configuration for the Conekta Tokenizer
 */
data class TokenizerConfig(
    val publicKey: String,
    val merchantName: String = "Demo Store",
    val collectCardholderName: Boolean = true,
    val baseUrl: String = PRODUCTION_BASE_URL,
    val rsaPublicKey: String = PRODUCTION_RSA_KEY,
)

private const val PRODUCTION_BASE_URL = "https://api.conekta.io/"

@Suppress("MaxLineLength")
private const val PRODUCTION_RSA_KEY =
    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjet2Jm4iPJTqDlW64tEG" +
        "I9/dJTJAcn3OQdHrEwNXCz0/Rewqcv/Hm+V0klsUiS9h2W5CLC42q6wGhtl9Buu" +
        "5vefuLVyxc8klEEjrSz/5AgfZ4HvzatbVX0KQhHI1j+caOjatDHM/ih13Rj7HIJF" +
        "nAcutRB9vyFiCVluqRhlB9/64sqGtVmxJAir7WJp4TmpPvSEqeGKQIb80Tq+FYY7" +
        "ftpMxQpsBT8B6y4Kn95ZfDH72H3yJezs/mExVB3M/OCBg+xt/c3dXp65JsbS482c" +
        "4KhkxxHChNn1Y/nZ8kFYzakRGhh0BMqkvkqtAwcQJK1xPx2jRELS1vj7OFfMR+3m" +
        "sSQIDAQAB"

/**
 * Result of a successful tokenization
 */
data class TokenResult(
    val token: String,
    val lastFour: String,
)

/**
 * Error during tokenization
 */
sealed class TokenizerError {
    data class ValidationError(
        val message: String,
    ) : TokenizerError()

    data class NetworkError(
        val message: String,
    ) : TokenizerError()

    data class ApiError(
        val code: String,
        val message: String,
    ) : TokenizerError()
}

/**
 * Card brand types
 */
enum class CardBrand {
    VISA,
    MASTERCARD,
    AMEX,
    UNKNOWN,
    ;

    /**
     * Check if this brand is UNKNOWN
     */
    fun isUNKNOWN(): Boolean = this == UNKNOWN
}
