package io.conekta.elements.checkout.api

import io.conekta.elements.checkout.models.CheckoutConfig
import io.conekta.elements.checkout.models.CheckoutError
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.localization.ConektaLanguage
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
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CheckoutApiServiceTest {
    private val testConfig =
        CheckoutConfig(
            checkoutRequestId = "dc5baf10-0f2b-4378-9f74-afa6bb418198",
            publicKey = "key_test_abc123",
            jwtToken = "jwt_test_token",
            baseUrl = "https://test.conekta.com/",
        )

    private fun createMockClient(
        statusCode: HttpStatusCode = HttpStatusCode.OK,
        responseBody: String = "",
        onRequest: ((String, String?, String?, String?) -> Unit)? = null,
    ): HttpClient =
        HttpClient(MockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            engine {
                addHandler { request ->
                    onRequest?.invoke(
                        request.url.toString(),
                        request.headers[HttpHeaders.Authorization],
                        request.headers["x-jwt-token"],
                        request.headers[HttpHeaders.AcceptLanguage],
                    )
                    respond(
                        content = responseBody,
                        status = statusCode,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
            }
        }

    @Test
    fun fetchCheckoutSuccessReturnsCheckoutResult() =
        runTest {
            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.OK,
                    responseBody =
                        """
                        {
                          "id":"0f3e251c-90b7-4846-9ecb-e48b447f25e4",
                          "amount":30000,
                          "allowedPaymentMethods":["Card","Apple","cash","BankTransfer"],
                          "providers":[
                            {"id":"647f8b322a0818004a414694","name":"datalogic","paymentMethod":"cash"},
                            {"id":"66df25a6af1debf142e80026","name":"bbva","paymentMethod":"cash"}
                          ],
                          "orderTemplate":{
                            "currency":"MXN",
                            "lineItems":[{"name":"Apple test 3","quantity":1,"unitPrice":30000}],
                            "taxLines":[{"description":"test","amount":2000}],
                            "discountLines":[],
                            "shippingLines":[]
                          }
                        }
                        """.trimIndent(),
                )

            val service = CheckoutApiService(config = testConfig, httpClient = mockClient)
            val result = service.fetchCheckout()

            assertTrue(result.isSuccess)
            val checkout = result.getOrThrow()
            assertEquals("0f3e251c-90b7-4846-9ecb-e48b447f25e4", checkout.orderId)
            assertEquals("0f3e251c-90b7-4846-9ecb-e48b447f25e4", checkout.checkoutId)
            assertEquals(30000L, checkout.amount)
            assertEquals("MXN", checkout.currency)
            assertEquals(
                listOf(
                    CheckoutPaymentMethods.CARD,
                    "apple",
                    "cash",
                    CheckoutPaymentMethods.BANK_TRANSFER,
                ),
                checkout.allowedPaymentMethods,
            )
            assertEquals(1, checkout.lineItems.size)
            assertEquals("Apple test 3", checkout.lineItems.first().name)
            assertEquals(30000L, checkout.lineItems.first().unitPrice)
            assertEquals(1, checkout.taxLines.size)
            assertEquals("test", checkout.taxLines.first().description)
            assertEquals(2000L, checkout.taxLines.first().amount)
            assertEquals(2, checkout.providers.size)
            assertEquals("datalogic", checkout.providers.first().name)
            assertEquals(CheckoutPaymentMethods.CASH, checkout.providers.first().paymentMethod)
        }

    @Test
    fun fetchCheckoutOldOrderResponseStillSupported() =
        runTest {
            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.OK,
                    responseBody =
                        """
                        {
                          "id":"ord_legacy",
                          "amount":12000,
                          "currency":"MXN",
                          "line_items":{"data":[{"name":"Aretes Tres Círculos Numerales","quantity":1,"unit_price":10000}]},
                          "tax_lines":{"data":[{"description":"test","amount":2000}]},
                          "checkout":{
                            "id":"chk_legacy",
                            "allowed_payment_methods":["card","cash","bank_transfer"]
                          }
                        }
                        """.trimIndent(),
                )

            val service = CheckoutApiService(config = testConfig, httpClient = mockClient)
            val result = service.fetchCheckout()

            assertTrue(result.isSuccess)
            val checkout = result.getOrThrow()
            assertEquals("ord_legacy", checkout.orderId)
            assertEquals("chk_legacy", checkout.checkoutId)
            assertEquals(12000L, checkout.amount)
            assertEquals("MXN", checkout.currency)
            assertEquals(
                listOf(
                    CheckoutPaymentMethods.CARD,
                    CheckoutPaymentMethods.CASH,
                    CheckoutPaymentMethods.BANK_TRANSFER,
                ),
                checkout.allowedPaymentMethods,
            )
        }

    @Test
    fun fetchCheckoutSendsExpectedHeaders() =
        runTest {
            var capturedUrl = ""
            var capturedAuthorization: String? = null
            var capturedJwt: String? = null
            var capturedAcceptLanguage: String? = null

            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.OK,
                    responseBody =
                        """
                        {
                          "id":"0f3e251c-90b7-4846-9ecb-e48b447f25e4",
                          "amount":30000,
                          "allowedPaymentMethods":["Card"],
                          "orderTemplate":{"currency":"MXN","lineItems":[],"taxLines":[],"discountLines":[],"shippingLines":[]}
                        }
                        """.trimIndent(),
                    onRequest = { url, authorization, jwt, acceptLanguage ->
                        capturedUrl = url
                        capturedAuthorization = authorization
                        capturedJwt = jwt
                        capturedAcceptLanguage = acceptLanguage
                    },
                )

            val service = CheckoutApiService(config = testConfig, httpClient = mockClient)
            val result = service.fetchCheckout()

            assertTrue(result.isSuccess)
            assertEquals(
                "https://test.conekta.com/checkout-requests/dc5baf10-0f2b-4378-9f74-afa6bb418198",
                capturedUrl,
            )
            assertEquals("Bearer key_test_abc123", capturedAuthorization)
            assertEquals("jwt_test_token", capturedJwt)
            assertEquals(ConektaLanguage.ES, capturedAcceptLanguage)
        }

    @Test
    fun fetchCheckoutSendsAcceptLanguageFromConfig() =
        runTest {
            var capturedAcceptLanguage: String? = null
            val config =
                testConfig.copy(
                    languageTag = ConektaLanguage.EN,
                )

            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.OK,
                    responseBody =
                        """
                        {
                          "id":"0f3e251c-90b7-4846-9ecb-e48b447f25e4",
                          "amount":30000,
                          "allowedPaymentMethods":["Card"],
                          "orderTemplate":{"currency":"MXN","lineItems":[],"taxLines":[],"discountLines":[],"shippingLines":[]}
                        }
                        """.trimIndent(),
                    onRequest = { _, _, _, acceptLanguage ->
                        capturedAcceptLanguage = acceptLanguage
                    },
                )

            val service = CheckoutApiService(config = config, httpClient = mockClient)
            val result = service.fetchCheckout()

            assertTrue(result.isSuccess)
            assertEquals(ConektaLanguage.EN, capturedAcceptLanguage)
        }

    @Test
    fun fetchCheckoutApiErrorReturnsFailure() =
        runTest {
            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.UnprocessableEntity,
                    responseBody =
                        """
                        {
                          "object":"error",
                          "type":"invalid_request_error",
                          "message":"Request is invalid",
                          "message_to_purchaser":"No fue posible procesar"
                        }
                        """.trimIndent(),
                )

            val service = CheckoutApiService(config = testConfig, httpClient = mockClient)
            val result = service.fetchCheckout()

            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertIs<CheckoutApiException>(exception)
            val apiError = assertIs<CheckoutError.ApiError>(exception.checkoutError)
            assertEquals("invalid_request_error", apiError.code)
            assertEquals("No fue posible procesar", apiError.message)
        }

    @Test
    fun fetchCheckoutUnexpectedErrorReturnsNetworkFailure() =
        runTest {
            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.InternalServerError,
                    responseBody = "Internal Server Error",
                )

            val service = CheckoutApiService(config = testConfig, httpClient = mockClient)
            val result = service.fetchCheckout()

            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertIs<CheckoutApiException>(exception)
            assertIs<CheckoutError.NetworkError>(exception.checkoutError)
        }
}
