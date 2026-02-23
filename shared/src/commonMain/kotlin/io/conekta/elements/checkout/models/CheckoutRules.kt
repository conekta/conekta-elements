package io.conekta.elements.checkout.models

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
        if (config.checkoutRequestId.isBlank()) {
            return CheckoutError.ValidationError(messages.checkoutRequestIdRequired)
        }
        if (config.publicKey.isBlank()) {
            return CheckoutError.ValidationError(messages.publicKeyRequired)
        }
        if (config.jwtToken.isBlank()) {
            return CheckoutError.ValidationError(messages.jwtTokenRequired)
        }
        return null
    }
}
