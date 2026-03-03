package io.conekta.elements.checkout.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CheckoutRulesTest {
    @Test
    fun filterSupportedMethods_keepsOnlyV1Methods() {
        val methods =
            listOf(
                CheckoutPaymentMethods.CARD,
                "bnpl",
                CheckoutPaymentMethods.CASH,
                "pay_by_bank",
                CheckoutPaymentMethods.BANK_TRANSFER,
                "apple",
            )

        val result = CheckoutMethodPolicy.filterSupportedMethods(methods)

        assertEquals(
            listOf(
                CheckoutPaymentMethods.CARD,
                CheckoutPaymentMethods.CASH,
                CheckoutPaymentMethods.BANK_TRANSFER,
            ),
            result,
        )
    }

    @Test
    fun selectDefaultSupportedMethod_returnsFirstSupported() {
        val methods =
            listOf(
                "bnpl",
                CheckoutPaymentMethods.CASH,
                CheckoutPaymentMethods.BANK_TRANSFER,
            )

        assertEquals(CheckoutPaymentMethods.CASH, CheckoutMethodPolicy.selectDefaultSupportedMethod(methods))
    }

    @Test
    fun selectDefaultSupportedMethod_returnsNull_whenNoSupportedMethodExists() {
        assertNull(CheckoutMethodPolicy.selectDefaultSupportedMethod(listOf("bnpl", "apple", "pay_by_bank")))
    }

    @Test
    fun validateConfig_returnsError_whenCheckoutRequestIdIsBlank() {
        val error =
            CheckoutConfigValidator.validate(
                config = CheckoutConfig(checkoutRequestId = "", publicKey = "pk", jwtToken = "jwt"),
                messages = messages(),
            )

        assertEquals("checkout_request_id_required", error?.message)
    }

    @Test
    fun validateConfig_returnsError_whenPublicKeyIsBlank() {
        val error =
            CheckoutConfigValidator.validate(
                config = CheckoutConfig(checkoutRequestId = "checkout", publicKey = "", jwtToken = "jwt"),
                messages = messages(),
            )

        assertEquals("public_key_required", error?.message)
    }

    @Test
    fun validateConfig_returnsError_whenJwtTokenIsBlank() {
        val error =
            CheckoutConfigValidator.validate(
                config = CheckoutConfig(checkoutRequestId = "checkout", publicKey = "pk", jwtToken = ""),
                messages = messages(),
            )

        assertEquals("jwt_token_required", error?.message)
    }

    @Test
    fun validateConfig_returnsNull_whenConfigIsValid() {
        val error =
            CheckoutConfigValidator.validate(
                config = CheckoutConfig(checkoutRequestId = "checkout", publicKey = "pk", jwtToken = "jwt"),
                messages = messages(),
            )

        assertNull(error)
    }

    private fun messages() =
        CheckoutConfigValidationMessages(
            checkoutRequestIdRequired = "checkout_request_id_required",
            publicKeyRequired = "public_key_required",
            jwtTokenRequired = "jwt_token_required",
        )
}
