package io.conekta.elements.checkout.models

import io.conekta.elements.testfixtures.TokenizerApiFixtures
import io.conekta.elements.tokenizer.api.TokenizerApiService
import io.conekta.elements.tokenizer.crypto.CardEncryptor
import io.conekta.elements.tokenizer.models.TokenizerConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CheckoutPaymentMethodTest {
    private fun createMockClient(
        statusCode: HttpStatusCode = HttpStatusCode.OK,
        responseBody: String = TokenizerApiFixtures.tokenResponsePayload(id = "tok_checkout"),
    ): HttpClient =
        HttpClient(MockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            engine {
                addHandler {
                    respond(
                        content = responseBody,
                        status = statusCode,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
            }
        }

    private fun tokenizerService(): TokenizerApiService =
        TokenizerApiService(
            config =
                TokenizerConfig(
                    publicKey = "key_test_123",
                    baseUrl = "https://test.conekta.com",
                    rsaPublicKey = "rsa_key",
                ),
            httpClient = createMockClient(),
            cryptoService = CardEncryptor { plaintext, _ -> "enc:$plaintext" },
        )

    @Test
    fun `from returns cash method and resolveTokenId is null`() =
        runTest {
            val method =
                CheckoutPaymentMethod.from(
                    methodKey = CheckoutPaymentMethods.CASH,
                    tokenizerService = tokenizerService(),
                    cardNumber = "",
                    expiryDate = "",
                    cvv = "",
                    cardholderName = "",
                )

            assertTrue(method is CheckoutPaymentMethod.Cash)
            assertEquals(CheckoutPaymentMethods.CASH, method.methodKey)
            assertNull(method.resolveTokenId())
        }

    @Test
    fun `from returns bank transfer method and resolveTokenId is null`() =
        runTest {
            val method =
                CheckoutPaymentMethod.from(
                    methodKey = CheckoutPaymentMethods.BANK_TRANSFER,
                    tokenizerService = tokenizerService(),
                    cardNumber = "",
                    expiryDate = "",
                    cvv = "",
                    cardholderName = "",
                )

            assertTrue(method is CheckoutPaymentMethod.BankTransfer)
            assertEquals(CheckoutPaymentMethods.BANK_TRANSFER, method.methodKey)
            assertNull(method.resolveTokenId())
        }

    @Test
    fun `from defaults to cash method for unknown method key`() =
        runTest {
            val method =
                CheckoutPaymentMethod.from(
                    methodKey = "unknown_method",
                    tokenizerService = tokenizerService(),
                    cardNumber = "",
                    expiryDate = "",
                    cvv = "",
                    cardholderName = "",
                )

            assertTrue(method is CheckoutPaymentMethod.Cash)
            assertEquals(CheckoutPaymentMethods.CASH, method.methodKey)
        }

    @Test
    fun `card resolveTokenId tokenizes and returns token`() =
        runTest {
            val method =
                CheckoutPaymentMethod.from(
                    methodKey = CheckoutPaymentMethods.CARD,
                    tokenizerService = tokenizerService(),
                    cardNumber = "4242 4242 4242 4242",
                    expiryDate = "12/26",
                    cvv = "12a3",
                    cardholderName = "Test User",
                )

            assertTrue(method is CheckoutPaymentMethod.Card)
            assertEquals(CheckoutPaymentMethods.CARD, method.methodKey)
            assertEquals("tok_checkout", method.resolveTokenId())
        }
}
