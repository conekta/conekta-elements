package io.conekta.elements.tokenizer.models

import io.conekta.elements.validation.RequiredFieldValidationRule
import io.conekta.elements.validation.RequiredFieldValidator

data class TokenizerConfigValidationMessages(
    val publicKeyRequired: String,
)

object TokenizerConfigValidator {
    fun validate(
        config: TokenizerConfig,
        messages: TokenizerConfigValidationMessages,
    ): TokenizerError.ValidationError? {
        val missingRequiredMessage =
            RequiredFieldValidator.firstError(
                RequiredFieldValidationRule(
                    value = config.publicKey,
                    requiredMessage = messages.publicKeyRequired,
                ),
            )
        return missingRequiredMessage?.let { TokenizerError.ValidationError(it) }
    }
}
