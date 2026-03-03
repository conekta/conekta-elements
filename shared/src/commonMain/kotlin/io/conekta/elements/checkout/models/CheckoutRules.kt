package io.conekta.elements.checkout.models

import io.conekta.elements.validation.RequiredFieldValidationRule
import io.conekta.elements.validation.RequiredFieldValidator

object CheckoutMethodPolicy {
    val V1SupportedMethods: Set<String> =
        setOf(
            CheckoutPaymentMethods.CARD,
            CheckoutPaymentMethods.CASH,
            CheckoutPaymentMethods.BANK_TRANSFER,
        )

    fun filterSupportedMethods(allowedPaymentMethods: List<String>): List<String> =
        allowedPaymentMethods.filter { method -> method in V1SupportedMethods }

    fun selectDefaultSupportedMethod(allowedPaymentMethods: List<String>): String? =
        filterSupportedMethods(allowedPaymentMethods).firstOrNull()
}

data class CheckoutConfigValidationMessages(
    val checkoutRequestIdRequired: String,
    val publicKeyRequired: String,
    val jwtTokenRequired: String,
)

object CheckoutConfigValidator {
    fun validate(
        config: CheckoutConfig,
        messages: CheckoutConfigValidationMessages,
    ): CheckoutError.ValidationError? {
        val missingRequiredMessage =
            RequiredFieldValidator.firstError(
                RequiredFieldValidationRule(
                    value = config.checkoutRequestId,
                    requiredMessage = messages.checkoutRequestIdRequired,
                ),
                RequiredFieldValidationRule(
                    value = config.publicKey,
                    requiredMessage = messages.publicKeyRequired,
                ),
                RequiredFieldValidationRule(
                    value = config.jwtToken,
                    requiredMessage = messages.jwtTokenRequired,
                ),
            )

        return missingRequiredMessage?.let { CheckoutError.ValidationError(it) }
    }
}
