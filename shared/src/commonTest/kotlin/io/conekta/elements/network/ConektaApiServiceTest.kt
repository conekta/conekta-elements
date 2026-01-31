package io.conekta.elements.network

import io.conekta.elements.network.models.Order
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
import kotlin.test.assertNotNull

class ConektaApiServiceTest {
    private val testOrderJson =
        """
        {
            "id": "ord_test123",
            "livemode": false,
            "amount": 12599,
            "currency": "MXN",
            "payment_status": "pending",
            "customer_info": {
                "name": "Test User",
                "email": "test@example.com",
                "phone": "5512345678"
            }
        }
        """.trimIndent()

    private fun createMockService(
        statusCode: HttpStatusCode = HttpStatusCode.OK,
        responseBody: String = testOrderJson,
    ): ConektaApiService {
        val mockEngine =
            MockEngine { _ ->
                respond(
                    content = responseBody,
                    status = statusCode,
                    headers =
                        headersOf(
                            HttpHeaders.ContentType,
                            ContentType.Application.Json.toString(),
                        ),
                )
            }
        val mockClient =
            HttpClient(mockEngine) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                        },
                    )
                }
            }
        return ConektaApiService(
            config = ConektaConfig(apiKey = "test_key"),
            client = mockClient,
        )
    }

    @Test
    fun getOrderReturnsSuccessOnOk() =
        runTest {
            val service = createMockService()
            val result = service.getOrder("ord_test123")

            assertIs<ApiResult.Success<Order>>(result)
            assertEquals("ord_test123", result.data.id)
            assertEquals(12599L, result.data.amount)
            assertEquals("MXN", result.data.currency)
            val customerInfo = result.data.customerInfo
            assertNotNull(customerInfo)
            assertEquals("Test User", customerInfo.name)
        }

    @Test
    fun getOrderReturnsErrorOnNotFound() =
        runTest {
            val errorJson =
                """
                {
                    "type": "resource_not_found",
                    "log_id": "abc123",
                    "details": [{
                        "debug_message": "Order not found",
                        "message": "El recurso no fue encontrado",
                        "code": "not_found"
                    }]
                }
                """.trimIndent()
            val service =
                createMockService(
                    statusCode = HttpStatusCode.NotFound,
                    responseBody = errorJson,
                )
            val result = service.getOrder("ord_nonexistent")

            assertIs<ApiResult.Error>(result)
            assertEquals(404, result.httpStatusCode)
            assertEquals("El recurso no fue encontrado", result.message)
        }

    @Test
    fun getOrderReturnsErrorOnUnauthorized() =
        runTest {
            val errorJson =
                """
                {
                    "type": "authentication_error",
                    "log_id": "def456",
                    "details": [{
                        "debug_message": "Invalid API key",
                        "message": "Clave de API invalida",
                        "code": "invalid_api_key"
                    }]
                }
                """.trimIndent()
            val service =
                createMockService(
                    statusCode = HttpStatusCode.Unauthorized,
                    responseBody = errorJson,
                )
            val result = service.getOrder("ord_test123")

            assertIs<ApiResult.Error>(result)
            assertEquals(401, result.httpStatusCode)
        }
}
