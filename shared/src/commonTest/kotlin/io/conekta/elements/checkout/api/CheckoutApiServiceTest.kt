package io.conekta.elements.checkout.api

import io.conekta.elements.checkout.models.CheckoutConfig
import io.conekta.elements.checkout.models.CheckoutError
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.checkout.models.CurrencyCodes
import io.conekta.elements.localization.ConektaLanguage
import io.conekta.elements.testfixtures.CheckoutApiFixtures
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CheckoutApiServiceTest {
    private val testCheckoutRequestId = CheckoutApiFixtures.randomUuid()
    private val testConfig =
        CheckoutConfig(
            checkoutRequestId = testCheckoutRequestId,
            publicKey = "key_test_abc123",
            jwtToken = "jwt_test_token",
            baseUrl = "https://test.conekta.com",
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
            val expectedCheckoutId = CheckoutApiFixtures.randomUuid()
            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.OK,
                    responseBody =
                        CheckoutApiFixtures.checkoutRequestPayload(
                            allowedPaymentMethods =
                                listOf("Card", "Apple", "cash_in", "bbva_cash_in", "BankTransfer"),
                            includeProviders = true,
                            id = expectedCheckoutId,
                        ),
                )

            val service = CheckoutApiService(config = testConfig, httpClient = mockClient)
            val result = service.fetchCheckout()

            assertTrue(result.isSuccess)
            val checkout = result.getOrThrow()
            assertEquals(expectedCheckoutId, checkout.orderId)
            assertEquals(expectedCheckoutId, checkout.checkoutId)
            assertEquals(30000, checkout.amount)
            assertEquals(CurrencyCodes.MXN, checkout.currency)
            assertEquals(
                listOf(
                    CheckoutPaymentMethods.CARD,
                    "apple",
                    "cash_in",
                    "bbva_cash_in",
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
            assertEquals("farmacias_del_ahorro", checkout.providers.first().name)
            assertEquals(CheckoutPaymentMethods.CASH, checkout.providers.first().paymentMethod)
        }

    @Test
    fun fetchCheckoutOldOrderResponseStillSupported() =
        runTest {
            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.OK,
                    responseBody = CheckoutApiFixtures.legacyCheckoutOrderPayload(),
                )

            val service = CheckoutApiService(config = testConfig, httpClient = mockClient)
            val result = service.fetchCheckout()

            assertTrue(result.isSuccess)
            val checkout = result.getOrThrow()
            assertEquals("ord_legacy", checkout.orderId)
            assertEquals("chk_legacy", checkout.checkoutId)
            assertEquals(12000, checkout.amount)
            assertEquals(CurrencyCodes.MXN, checkout.currency)
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
                    responseBody = CheckoutApiFixtures.checkoutRequestPayload(allowedPaymentMethods = listOf("Card")),
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
                "https://test.conekta.com/checkout-bff/v1/checkout-requests/$testCheckoutRequestId",
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
                    responseBody = CheckoutApiFixtures.checkoutRequestPayload(allowedPaymentMethods = listOf("Card")),
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
                        CheckoutApiFixtures.apiErrorPayload(
                            type = "invalid_request_error",
                            message = "Request is invalid",
                            messageToPurchaser = "No fue posible procesar",
                        ),
                )

            val service = CheckoutApiService(config = testConfig, httpClient = mockClient)
            val result = service.fetchCheckout()

            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertIs<CheckoutApiException>(exception)
            val apiError = assertIs<CheckoutError.ApiError>(exception.checkoutError)
            assertEquals("invalid_request_error", apiError.code)
            assertEquals("Request is invalid", apiError.message)
        }

    @Test
    fun fetchCheckoutApiErrorPrefersNestedDetailsMessage() =
        runTest {
            val nestedDetailMessage =
                "Invalid purchase due to a card issued outside of Mexico. Please try with another card or payment method."
            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.BadRequest,
                    responseBody = CheckoutApiFixtures.nestedDetailsErrorPayload(nestedDetailMessage),
                )

            val service = CheckoutApiService(config = testConfig, httpClient = mockClient)
            val result = service.fetchCheckout()

            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertIs<CheckoutApiException>(exception)
            val apiError = assertIs<CheckoutError.ApiError>(exception.checkoutError)
            assertEquals("parameter_validation_error", apiError.code)
            assertEquals(nestedDetailMessage, apiError.message)
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

    // --- createOrder tests ---

    private data class CapturedRequest(
        val url: String = "",
        val method: HttpMethod = HttpMethod.Get,
        val body: String = "",
        val authorization: String? = null,
        val jwtToken: String? = null,
    )

    private fun createPostMockClient(
        statusCode: HttpStatusCode = HttpStatusCode.OK,
        responseBody: String = "",
        onRequest: ((CapturedRequest) -> Unit)? = null,
    ): HttpClient =
        HttpClient(MockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            engine {
                addHandler { request ->
                    val bodyText = request.body.toByteArray().decodeToString()
                    onRequest?.invoke(
                        CapturedRequest(
                            url = request.url.toString(),
                            method = request.method,
                            body = bodyText,
                            authorization = request.headers[HttpHeaders.Authorization],
                            jwtToken = request.headers["x-jwt-token"],
                        ),
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
    fun createOrderCashSuccess() =
        runTest {
            val mockClient =
                createPostMockClient(
                    statusCode = HttpStatusCode.OK,
                    responseBody = """{"id":"ord_cash_123"}""",
                )

            val service = CheckoutApiService(config = testConfig, httpClient = mockClient)
            val result = service.createOrder(CheckoutPaymentMethods.CASH)

            assertTrue(result.isSuccess)
            assertEquals("ord_cash_123", result.getOrThrow().orderId)
        }

    @Test
    fun createOrderBankTransferSuccess() =
        runTest {
            val mockClient =
                createPostMockClient(
                    statusCode = HttpStatusCode.OK,
                    responseBody = """{"id":"ord_bank_456"}""",
                )

            val service = CheckoutApiService(config = testConfig, httpClient = mockClient)
            val result = service.createOrder(CheckoutPaymentMethods.BANK_TRANSFER)

            assertTrue(result.isSuccess)
            assertEquals("ord_bank_456", result.getOrThrow().orderId)
        }

    @Test
    fun createOrderCardWithTokenSuccess() =
        runTest {
            var capturedBody = ""
            val mockClient =
                createPostMockClient(
                    statusCode = HttpStatusCode.OK,
                    responseBody = """{"id":"ord_card_789"}""",
                    onRequest = { captured -> capturedBody = captured.body },
                )

            val service = CheckoutApiService(config = testConfig, httpClient = mockClient)
            val result = service.createOrder(CheckoutPaymentMethods.CARD, tokenId = "tok_test_abc")

            assertTrue(result.isSuccess)
            assertEquals("ord_card_789", result.getOrThrow().orderId)

            val bodyJson = Json.decodeFromString<JsonObject>(capturedBody)
            assertEquals("tok_test_abc", bodyJson["tokenId"]?.jsonPrimitive?.content)
            assertEquals("Card", bodyJson["paymentMethod"]?.jsonPrimitive?.content)
        }

    @Test
    fun createOrderSendsCorrectUrlAndBody() =
        runTest {
            var captured = CapturedRequest()
            val mockClient =
                createPostMockClient(
                    statusCode = HttpStatusCode.OK,
                    responseBody = """{"id":"ord_test"}""",
                    onRequest = { captured = it },
                )

            val service = CheckoutApiService(config = testConfig, httpClient = mockClient)
            service.createOrder(CheckoutPaymentMethods.CASH)

            assertEquals("https://test.conekta.com/checkout-bff/v2/order", captured.url)
            assertEquals(HttpMethod.Post, captured.method)
            assertEquals("Bearer key_test_abc123", captured.authorization)
            assertEquals("jwt_test_token", captured.jwtToken)

            val bodyJson = Json.decodeFromString<JsonObject>(captured.body)
            assertEquals(testCheckoutRequestId, bodyJson["checkoutRequestId"]?.jsonPrimitive?.content)
            assertEquals("Cash", bodyJson["paymentMethod"]?.jsonPrimitive?.content)
            assertTrue(bodyJson["tokenId"] is JsonNull || bodyJson["tokenId"] == null)
        }

    @Test
    fun createOrderApiErrorReturnsFailure() =
        runTest {
            val mockClient =
                createPostMockClient(
                    statusCode = HttpStatusCode.UnprocessableEntity,
                    responseBody =
                        CheckoutApiFixtures.apiErrorPayload(
                            type = "invalid_request_error",
                            message = "Invalid payment method",
                            messageToPurchaser = "No fue posible procesar el pago",
                        ),
                )

            val service = CheckoutApiService(config = testConfig, httpClient = mockClient)
            val result = service.createOrder(CheckoutPaymentMethods.CASH)

            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertIs<CheckoutApiException>(exception)
            val apiError = assertIs<CheckoutError.ApiError>(exception.checkoutError)
            assertEquals("invalid_request_error", apiError.code)
            assertEquals("Invalid payment method", apiError.message)
        }

    @Test
    fun createOrderNetworkErrorReturnsFailure() =
        runTest {
            val mockClient =
                createPostMockClient(
                    statusCode = HttpStatusCode.InternalServerError,
                    responseBody = "Internal Server Error",
                )

            val service = CheckoutApiService(config = testConfig, httpClient = mockClient)
            val result = service.createOrder(CheckoutPaymentMethods.BANK_TRANSFER)

            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertIs<CheckoutApiException>(exception)
            assertIs<CheckoutError.NetworkError>(exception.checkoutError)
        }
}
