package io.conekta.elements.checkout.api

import io.conekta.elements.checkout.models.CheckoutConfig
import io.conekta.elements.checkout.models.CheckoutError
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
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
        onRequest: ((String, String?, String?) -> Unit)? = null,
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
                          "id":"ord_2zb4KeLHjraBbRJgs",
                          "amount":12000,
                          "currency":"MXN",
                          "line_items":{"data":[{"name":"Aretes Tres Círculos Numerales","quantity":1,"unit_price":10000}]},
                          "tax_lines":{"data":[{"description":"test","amount":2000}]},
                          "checkout":{
                            "id":"dc5baf10-0f2b-4378-9f74-afa6bb418198",
                            "allowed_payment_methods":["card","bnpl","cash","pay_by_bank","bank_transfer","apple"]
                          }
                        }
                        """.trimIndent(),
                )

            val service = CheckoutApiService(config = testConfig, httpClient = mockClient)
            val result = service.fetchCheckout()

            assertTrue(result.isSuccess)
            val checkout = result.getOrThrow()
            assertEquals("ord_2zb4KeLHjraBbRJgs", checkout.orderId)
            assertEquals("dc5baf10-0f2b-4378-9f74-afa6bb418198", checkout.checkoutId)
            assertEquals(12000L, checkout.amount)
            assertEquals("MXN", checkout.currency)
            assertEquals(
                listOf(
                    CheckoutPaymentMethods.CARD,
                    "bnpl",
                    CheckoutPaymentMethods.CASH,
                    "pay_by_bank",
                    CheckoutPaymentMethods.BANK_TRANSFER,
                    "apple",
                ),
                checkout.allowedPaymentMethods,
            )
            assertEquals(1, checkout.lineItems.size)
            assertEquals("Aretes Tres Círculos Numerales", checkout.lineItems.first().name)
            assertEquals(10000L, checkout.lineItems.first().unitPrice)
            assertEquals(1, checkout.taxLines.size)
            assertEquals("test", checkout.taxLines.first().description)
            assertEquals(2000L, checkout.taxLines.first().amount)
        }

    @Test
    fun fetchCheckoutSendsExpectedHeaders() =
        runTest {
            var capturedUrl = ""
            var capturedAuthorization: String? = null
            var capturedJwt: String? = null

            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.OK,
                    responseBody =
                        """
                        {
                          "id":"ord_1",
                          "amount":1000,
                          "currency":"MXN",
                          "checkout":{"id":"chk_1","allowed_payment_methods":["card"]}
                        }
                        """.trimIndent(),
                    onRequest = { url, authorization, jwt ->
                        capturedUrl = url
                        capturedAuthorization = authorization
                        capturedJwt = jwt
                    },
                )

            val service = CheckoutApiService(config = testConfig, httpClient = mockClient)
            val result = service.fetchCheckout()

            assertTrue(result.isSuccess)
            assertEquals(
                "https://test.conekta.com/checkouts/dc5baf10-0f2b-4378-9f74-afa6bb418198",
                capturedUrl,
            )
            assertEquals("Bearer key_test_abc123", capturedAuthorization)
            assertEquals("jwt_test_token", capturedJwt)
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
