package io.conekta.elements.tokenizer.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class TokenizerConfigTest {
    @Test
    fun `TokenizerConfig stores publicKey and merchantName`() {
        val config =
            TokenizerConfig(
                publicKey = "key_test_123",
                merchantName = "My Store",
            )
        assertEquals("key_test_123", config.publicKey)
        assertEquals("My Store", config.merchantName)
    }

    @Test
    fun `TokenizerConfig has default merchantName`() {
        val config = TokenizerConfig(publicKey = "key_test_123")
        assertEquals("Demo Store", config.merchantName)
    }

    @Test
    fun `TokenizerConfig has default collectCardholderName true`() {
        val config = TokenizerConfig(publicKey = "key_test_123")
        assertTrue(config.collectCardholderName)
    }

    @Test
    fun `TokenizerConfig collectCardholderName can be set to false`() {
        val config =
            TokenizerConfig(
                publicKey = "key_test_123",
                collectCardholderName = false,
            )
        assertFalse(config.collectCardholderName)
    }

    @Test
    fun `TokenizerConfig equality works for same values`() {
        val config1 = TokenizerConfig(publicKey = "key_test_123", merchantName = "Store")
        val config2 = TokenizerConfig(publicKey = "key_test_123", merchantName = "Store")
        assertEquals(config1, config2)
    }

    @Test
    fun `TokenizerConfig inequality for different publicKey`() {
        val config1 = TokenizerConfig(publicKey = "key_test_123")
        val config2 = TokenizerConfig(publicKey = "key_test_456")
        assertNotEquals(config1, config2)
    }

    @Test
    fun `TokenizerConfig copy changes only specified fields`() {
        val original = TokenizerConfig(publicKey = "key_test_123", merchantName = "Store A")
        val copy = original.copy(merchantName = "Store B")
        assertEquals("key_test_123", copy.publicKey)
        assertEquals("Store B", copy.merchantName)
    }

    @Test
    fun `TokenizerConfig has default baseUrl pointing to production`() {
        val config = TokenizerConfig(publicKey = "key_test_123")
        assertEquals("https://api.conekta.io/", config.baseUrl)
    }

    @Test
    fun `TokenizerConfig has default rsaPublicKey for production`() {
        val config = TokenizerConfig(publicKey = "key_test_123")
        assertTrue(config.rsaPublicKey.isNotEmpty())
        assertTrue(config.rsaPublicKey.startsWith("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjet2"))
    }

    @Test
    fun `TokenizerConfig accepts custom baseUrl`() {
        val config =
            TokenizerConfig(
                publicKey = "key_test_123",
                baseUrl = "https://pay.stg.conekta.io/",
            )
        assertEquals("https://pay.stg.conekta.io/", config.baseUrl)
    }

    @Test
    fun `TokenizerConfig accepts custom rsaPublicKey`() {
        val config =
            TokenizerConfig(
                publicKey = "key_test_123",
                rsaPublicKey = "custom_rsa_key",
            )
        assertEquals("custom_rsa_key", config.rsaPublicKey)
    }

    @Test
    fun `secondary constructor with publicKey only uses all defaults`() {
        val config = TokenizerConfig("key_test_123")
        assertEquals("key_test_123", config.publicKey)
        assertEquals("Demo Store", config.merchantName)
        assertTrue(config.collectCardholderName)
        assertEquals("https://api.conekta.io/", config.baseUrl)
        assertTrue(config.rsaPublicKey.startsWith("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjet2"))
    }

    @Test
    fun `secondary constructor with three params uses default baseUrl and rsaPublicKey`() {
        val config = TokenizerConfig("key_test_123", "Custom Store", false)
        assertEquals("key_test_123", config.publicKey)
        assertEquals("Custom Store", config.merchantName)
        assertFalse(config.collectCardholderName)
        assertEquals("https://api.conekta.io/", config.baseUrl)
        assertTrue(config.rsaPublicKey.startsWith("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjet2"))
    }

    @Test
    fun `secondary constructors produce same result as primary with same values`() {
        val fromPrimary = TokenizerConfig(
            publicKey = "key_test_123",
            merchantName = "Demo Store",
            collectCardholderName = true,
        )
        val fromSecondary1 = TokenizerConfig("key_test_123")
        val fromSecondary3 = TokenizerConfig("key_test_123", "Demo Store", true)
        assertEquals(fromPrimary, fromSecondary1)
        assertEquals(fromPrimary, fromSecondary3)
    }

    @Test
    fun `TokenizerConfig equality considers baseUrl and rsaPublicKey`() {
        val config1 = TokenizerConfig(publicKey = "key_test_123", baseUrl = "https://a.com/")
        val config2 = TokenizerConfig(publicKey = "key_test_123", baseUrl = "https://b.com/")
        assertNotEquals(config1, config2)
    }
}

class TokenResultTest {
    @Test
    fun `TokenResult stores token and lastFour`() {
        val result =
            TokenResult(
                token = "tok_abc123",
                lastFour = "4242",
            )
        assertEquals("tok_abc123", result.token)
        assertEquals("4242", result.lastFour)
    }

    @Test
    fun `TokenResult equality works for same values`() {
        val result1 = TokenResult(token = "tok_abc", lastFour = "4242")
        val result2 = TokenResult(token = "tok_abc", lastFour = "4242")
        assertEquals(result1, result2)
    }

    @Test
    fun `TokenResult inequality for different token`() {
        val result1 = TokenResult(token = "tok_abc", lastFour = "4242")
        val result2 = TokenResult(token = "tok_xyz", lastFour = "4242")
        assertNotEquals(result1, result2)
    }

    @Test
    fun `TokenResult inequality for different lastFour`() {
        val result1 = TokenResult(token = "tok_abc", lastFour = "4242")
        val result2 = TokenResult(token = "tok_abc", lastFour = "1234")
        assertNotEquals(result1, result2)
    }
}

class TokenizerErrorTest {
    @Test
    fun `ValidationError stores message`() {
        val error = TokenizerError.ValidationError("Card number is invalid")
        assertEquals("Card number is invalid", error.message)
    }

    @Test
    fun `NetworkError stores message`() {
        val error = TokenizerError.NetworkError("Connection timeout")
        assertEquals("Connection timeout", error.message)
    }

    @Test
    fun `ApiError stores code and message`() {
        val error = TokenizerError.ApiError(code = "422", message = "Unprocessable entity")
        assertEquals("422", error.code)
        assertEquals("Unprocessable entity", error.message)
    }

    @Test
    fun `ValidationError is TokenizerError`() {
        val error: TokenizerError = TokenizerError.ValidationError("test")
        assertIs<TokenizerError.ValidationError>(error)
    }

    @Test
    fun `NetworkError is TokenizerError`() {
        val error: TokenizerError = TokenizerError.NetworkError("test")
        assertIs<TokenizerError.NetworkError>(error)
    }

    @Test
    fun `ApiError is TokenizerError`() {
        val error: TokenizerError = TokenizerError.ApiError("500", "test")
        assertIs<TokenizerError.ApiError>(error)
    }

    @Test
    fun `when expression covers all TokenizerError variants`() {
        val errors: List<TokenizerError> =
            listOf(
                TokenizerError.ValidationError("v"),
                TokenizerError.NetworkError("n"),
                TokenizerError.ApiError("c", "a"),
            )
        val types =
            errors.map { error ->
                when (error) {
                    is TokenizerError.ValidationError -> "validation"
                    is TokenizerError.NetworkError -> "network"
                    is TokenizerError.ApiError -> "api"
                }
            }
        assertEquals(listOf("validation", "network", "api"), types)
    }

    @Test
    fun `ValidationError equality works`() {
        val error1 = TokenizerError.ValidationError("same")
        val error2 = TokenizerError.ValidationError("same")
        assertEquals(error1, error2)
    }

    @Test
    fun `ApiError inequality for different codes`() {
        val error1 = TokenizerError.ApiError("400", "Bad request")
        val error2 = TokenizerError.ApiError("500", "Bad request")
        assertNotEquals(error1, error2)
    }
}

class CardBrandTest {
    @Test
    fun `CardBrand has four values`() {
        assertEquals(4, CardBrand.entries.size)
    }

    @Test
    fun `CardBrand VISA isUNKNOWN returns false`() {
        assertFalse(CardBrand.VISA.isUNKNOWN())
    }

    @Test
    fun `CardBrand MASTERCARD isUNKNOWN returns false`() {
        assertFalse(CardBrand.MASTERCARD.isUNKNOWN())
    }

    @Test
    fun `CardBrand AMEX isUNKNOWN returns false`() {
        assertFalse(CardBrand.AMEX.isUNKNOWN())
    }

    @Test
    fun `CardBrand UNKNOWN isUNKNOWN returns true`() {
        assertTrue(CardBrand.UNKNOWN.isUNKNOWN())
    }

    @Test
    fun `CardBrand valueOf returns correct brand`() {
        assertEquals(CardBrand.VISA, CardBrand.valueOf("VISA"))
        assertEquals(CardBrand.MASTERCARD, CardBrand.valueOf("MASTERCARD"))
        assertEquals(CardBrand.AMEX, CardBrand.valueOf("AMEX"))
        assertEquals(CardBrand.UNKNOWN, CardBrand.valueOf("UNKNOWN"))
    }

    @Test
    fun `CardBrand name returns correct string`() {
        assertEquals("VISA", CardBrand.VISA.name)
        assertEquals("MASTERCARD", CardBrand.MASTERCARD.name)
        assertEquals("AMEX", CardBrand.AMEX.name)
        assertEquals("UNKNOWN", CardBrand.UNKNOWN.name)
    }
}
