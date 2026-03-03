package io.conekta.elements.checkout.models

import io.conekta.elements.localization.ConektaLanguage
import io.conekta.elements.network.ConektaServers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CheckoutModelsTest {
    @Test
    fun checkoutConfigPrimaryConstructorKeepsProvidedValues() {
        val config =
            CheckoutConfig(
                checkoutRequestId = "checkout-request-id",
                publicKey = "public-key",
                jwtToken = "jwt-token",
                merchantName = "My Store",
                baseUrl = "https://checkout.test",
                languageTag = ConektaLanguage.EN,
                tokenizerBaseUrl = "https://tokenizer.test",
                tokenizerRsaPublicKey = "rsa-public-key",
            )

        assertEquals("checkout-request-id", config.checkoutRequestId)
        assertEquals("public-key", config.publicKey)
        assertEquals("jwt-token", config.jwtToken)
        assertEquals("My Store", config.merchantName)
        assertEquals("https://checkout.test", config.baseUrl)
        assertEquals(ConektaLanguage.EN, config.languageTag)
        assertEquals("https://tokenizer.test", config.tokenizerBaseUrl)
        assertEquals("rsa-public-key", config.tokenizerRsaPublicKey)
    }

    @Test
    fun checkoutConfigConstructorWithTokenizerBaseUrlUsesExpectedDefaults() {
        val config =
            CheckoutConfig(
                checkoutRequestId = "checkout-request-id",
                publicKey = "public-key",
                jwtToken = "jwt-token",
                merchantName = "My Store",
                baseUrl = "https://checkout.test",
                tokenizerBaseUrl = "https://tokenizer.test",
            )

        assertEquals("My Store", config.merchantName)
        assertEquals("https://checkout.test", config.baseUrl)
        assertEquals(ConektaLanguage.ES, config.languageTag)
        assertEquals("https://tokenizer.test", config.tokenizerBaseUrl)
        assertNull(config.tokenizerRsaPublicKey)
    }

    @Test
    fun checkoutConfigConstructorWithTokenizerBaseUrlAndRsaUsesExpectedDefaults() {
        val config =
            CheckoutConfig(
                checkoutRequestId = "checkout-request-id",
                publicKey = "public-key",
                jwtToken = "jwt-token",
                merchantName = "My Store",
                baseUrl = "https://checkout.test",
                tokenizerBaseUrl = "https://tokenizer.test",
                tokenizerRsaPublicKey = "rsa-public-key",
            )

        assertEquals(ConektaLanguage.ES, config.languageTag)
        assertEquals("https://tokenizer.test", config.tokenizerBaseUrl)
        assertEquals("rsa-public-key", config.tokenizerRsaPublicKey)
    }

    @Test
    fun checkoutConfigConstructorWithMerchantNameUsesProductionDefaults() {
        val config =
            CheckoutConfig(
                checkoutRequestId = "checkout-request-id",
                publicKey = "public-key",
                jwtToken = "jwt-token",
                merchantName = "My Store",
            )

        assertEquals("My Store", config.merchantName)
        assertEquals(ConektaServers.CHECKOUT_PRODUCTION_BASE_URL, config.baseUrl)
        assertEquals(ConektaLanguage.ES, config.languageTag)
        assertEquals(ConektaServers.TOKENIZER_PRODUCTION_BASE_URL, config.tokenizerBaseUrl)
        assertNull(config.tokenizerRsaPublicKey)
    }

    @Test
    fun checkoutConfigConstructorWithMerchantNameAndBaseUrlUsesTokenizerDefault() {
        val config =
            CheckoutConfig(
                checkoutRequestId = "checkout-request-id",
                publicKey = "public-key",
                jwtToken = "jwt-token",
                merchantName = "My Store",
                baseUrl = "https://checkout.test",
            )

        assertEquals("https://checkout.test", config.baseUrl)
        assertEquals(ConektaLanguage.ES, config.languageTag)
        assertEquals(ConektaServers.TOKENIZER_PRODUCTION_BASE_URL, config.tokenizerBaseUrl)
        assertNull(config.tokenizerRsaPublicKey)
    }

    @Test
    fun checkoutConfigConstructorWithOnlyRequiredFieldsUsesAllDefaults() {
        val config =
            CheckoutConfig(
                checkoutRequestId = "checkout-request-id",
                publicKey = "public-key",
                jwtToken = "jwt-token",
            )

        assertEquals("Demo Store", config.merchantName)
        assertEquals(ConektaServers.CHECKOUT_PRODUCTION_BASE_URL, config.baseUrl)
        assertEquals(ConektaLanguage.ES, config.languageTag)
        assertEquals(ConektaServers.TOKENIZER_PRODUCTION_BASE_URL, config.tokenizerBaseUrl)
        assertNull(config.tokenizerRsaPublicKey)
    }

    @Test
    fun checkoutOrderAndChargeModelsExposeDefaultValues() {
        val order = CheckoutOrderResult(orderId = "ord_123")
        val charge = CheckoutCharge()
        val paymentMethod = CheckoutChargePaymentMethod()

        assertEquals("ord_123", order.orderId)
        assertEquals("", order.status)
        assertNull(order.nextAction)
        assertEquals("", order.urlRedirect)
        assertTrue(order.charges.isEmpty())

        assertEquals(0, charge.amount)
        assertEquals(CurrencyCodes.MXN, charge.currency)
        assertEquals("", charge.status)
        assertNull(charge.paymentMethod)

        assertEquals("", paymentMethod.type)
        assertEquals("", paymentMethod.reference)
        assertEquals("", paymentMethod.clabe)
        assertEquals("", paymentMethod.barcodeUrl)
        assertEquals(0L, paymentMethod.expiresAt)
        assertEquals("", paymentMethod.serviceName)
        assertEquals("", paymentMethod.storeName)
        assertEquals("", paymentMethod.provider)
        assertEquals("", paymentMethod.agreement)
        assertEquals("", paymentMethod.name)
        assertEquals("", paymentMethod.productType)
    }
}
