package io.conekta.elements.tokenizer.api

import io.conekta.elements.tokenizer.crypto.CardEncryptor
import io.conekta.elements.tokenizer.crypto.EncryptedCardData
import io.conekta.elements.tokenizer.models.TokenizerConfig
import io.conekta.elements.tokenizer.models.TokenizerError
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

class TokenizerApiServiceTest {
    private val testConfig =
        TokenizerConfig(
            publicKey = "key_test_abc123",
            merchantName = "Test Store",
            baseUrl = "https://test.conekta.com/",
            rsaPublicKey = "test_rsa_key",
        )

    private fun createMockClient(
        statusCode: HttpStatusCode = HttpStatusCode.OK,
        responseBody: String = "",
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
                        headers =
                            headersOf(
                                HttpHeaders.ContentType,
                                ContentType.Application.Json.toString(),
                            ),
                    )
                }
            }
        }

    private val fakeEncryptor =
        object : CardEncryptor {
            override fun encryptCardData(
                cardJson: String,
                rsaPublicKeyBase64: String,
            ): EncryptedCardData =
                EncryptedCardData(
                    encryptedData = "fake_encrypted_data",
                    encryptedKey = "fake_encrypted_key",
                )
        }

    @Test
    fun tokenizeSuccessReturnsTokenResult() =
        runTest {
            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.OK,
                    responseBody =
                        """{"id":"tok_test_xyz789","livemode":false,"used":false,"object":"token"}""",
                )

            val service =
                TokenizerApiService(
                    config = testConfig,
                    httpClient = mockClient,
                    cryptoService = fakeEncryptor,
                )

            val result =
                service.tokenize(
                    cardNumber = "4242424242424242",
                    expMonth = "12",
                    expYear = "26",
                    cvc = "123",
                    cardholderName = "John Doe",
                )

            assertTrue(result.isSuccess, "Expected success but got: ${result.exceptionOrNull()}")
            val tokenResult = result.getOrThrow()
            assertEquals("tok_test_xyz789", tokenResult.token)
            assertEquals("4242", tokenResult.lastFour)
        }

    @Test
    fun tokenizeApiErrorReturnsFailure() =
        runTest {
            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.UnprocessableEntity,
                    responseBody =
                        """{
                        "object":"error",
                        "type":"invalid_request_error",
                        "message":"card number is invalid",
                        "message_to_purchaser":"The card could not be processed"
                    }""",
                )

            val service =
                TokenizerApiService(
                    config = testConfig,
                    httpClient = mockClient,
                    cryptoService = fakeEncryptor,
                )

            val result =
                service.tokenize(
                    cardNumber = "0000000000000000",
                    expMonth = "12",
                    expYear = "26",
                    cvc = "123",
                    cardholderName = "Jane Doe",
                )

            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertIs<TokenizerApiException>(exception)
            val apiError = assertIs<TokenizerError.ApiError>(exception.tokenizerError)
            assertEquals("invalid_request_error", apiError.code)
            assertEquals("The card could not be processed", apiError.message)
        }

    @Test
    fun tokenizeNetworkErrorReturnsFailure() =
        runTest {
            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.InternalServerError,
                    responseBody = "Internal Server Error",
                )

            val service =
                TokenizerApiService(
                    config = testConfig,
                    httpClient = mockClient,
                    cryptoService = fakeEncryptor,
                )

            val result =
                service.tokenize(
                    cardNumber = "4242424242424242",
                    expMonth = "12",
                    expYear = "26",
                    cvc = "123",
                    cardholderName = "Test User",
                )

            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertIs<TokenizerApiException>(exception)
            assertIs<TokenizerError.NetworkError>(exception.tokenizerError)
        }

    @Test
    fun tokenizeWith201CreatedReturnsSuccess() =
        runTest {
            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.Created,
                    responseBody =
                        """{"id":"tok_created_123","livemode":true,"used":false,"object":"token"}""",
                )

            val service =
                TokenizerApiService(
                    config = testConfig,
                    httpClient = mockClient,
                    cryptoService = fakeEncryptor,
                )

            val result =
                service.tokenize(
                    cardNumber = "5555555555554444",
                    expMonth = "06",
                    expYear = "28",
                    cvc = "456",
                    cardholderName = "Test",
                )

            assertTrue(result.isSuccess)
            assertEquals("tok_created_123", result.getOrThrow().token)
            assertEquals("4444", result.getOrThrow().lastFour)
        }

    @Test
    fun tokenizeLastFourExtractsFromFormattedNumber() =
        runTest {
            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.OK,
                    responseBody =
                        """{"id":"tok_fmt","livemode":false,"used":false,"object":"token"}""",
                )

            val service =
                TokenizerApiService(
                    config = testConfig,
                    httpClient = mockClient,
                    cryptoService = fakeEncryptor,
                )

            // Card number with spaces (as formatted input)
            val result =
                service.tokenize(
                    cardNumber = "4242 4242 4242 4242",
                    expMonth = "12",
                    expYear = "26",
                    cvc = "123",
                    cardholderName = "Test",
                )

            assertTrue(result.isSuccess)
            assertEquals("4242", result.getOrThrow().lastFour)
        }

    @Test
    fun tokenizeApiErrorFallsBackToMessageWhenPurchaserMessageEmpty() =
        runTest {
            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.BadRequest,
                    responseBody =
                        """{
                        "object":"error",
                        "type":"parameter_validation_error",
                        "message":"The card number is not valid",
                        "message_to_purchaser":""
                    }""",
                )

            val service =
                TokenizerApiService(
                    config = testConfig,
                    httpClient = mockClient,
                    cryptoService = fakeEncryptor,
                )

            val result =
                service.tokenize(
                    cardNumber = "1234",
                    expMonth = "12",
                    expYear = "26",
                    cvc = "123",
                    cardholderName = "Test",
                )

            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertIs<TokenizerApiException>(exception)
            val apiError = exception.tokenizerError as TokenizerError.ApiError
            assertEquals("The card number is not valid", apiError.message)
        }

    @Test
    fun tokenizePassesCardDataToEncryptor() =
        runTest {
            var capturedCardJson = ""
            var capturedRsaKey = ""

            val capturingEncryptor =
                object : CardEncryptor {
                    override fun encryptCardData(
                        cardJson: String,
                        rsaPublicKeyBase64: String,
                    ): EncryptedCardData {
                        capturedCardJson = cardJson
                        capturedRsaKey = rsaPublicKeyBase64
                        return EncryptedCardData("data", "key")
                    }
                }

            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.OK,
                    responseBody =
                        """{"id":"tok_cap","livemode":false,"used":false,"object":"token"}""",
                )

            val service =
                TokenizerApiService(
                    config = testConfig,
                    httpClient = mockClient,
                    cryptoService = capturingEncryptor,
                )

            service.tokenize(
                cardNumber = "4111111111111111",
                expMonth = "03",
                expYear = "29",
                cvc = "456",
                cardholderName = "Jane Smith",
            )

            assertTrue(capturedCardJson.contains("4111111111111111"), "Card number should be in JSON")
            assertTrue(capturedCardJson.contains("03"), "Exp month should be in JSON")
            assertTrue(capturedCardJson.contains("29"), "Exp year should be in JSON")
            assertTrue(capturedCardJson.contains("456"), "CVC should be in JSON")
            assertTrue(capturedCardJson.contains("Jane Smith"), "Cardholder name should be in JSON")
            assertEquals("test_rsa_key", capturedRsaKey, "RSA key from config should be passed")
        }

    @Test
    fun tokenizeSendsRequestToCorrectUrl() =
        runTest {
            var capturedUrl = ""

            val mockClient =
                HttpClient(MockEngine) {
                    install(ContentNegotiation) {
                        json(Json { ignoreUnknownKeys = true })
                    }
                    engine {
                        addHandler { request ->
                            capturedUrl = request.url.toString()
                            respond(
                                content =
                                    """{"id":"tok_url","livemode":false,"used":false,"object":"token"}""",
                                status = HttpStatusCode.OK,
                                headers =
                                    headersOf(
                                        HttpHeaders.ContentType,
                                        ContentType.Application.Json.toString(),
                                    ),
                            )
                        }
                    }
                }

            val service =
                TokenizerApiService(
                    config = testConfig,
                    httpClient = mockClient,
                    cryptoService = fakeEncryptor,
                )

            service.tokenize(
                cardNumber = "4242424242424242",
                expMonth = "12",
                expYear = "26",
                cvc = "123",
                cardholderName = "Test",
            )

            assertEquals("https://test.conekta.com/api/tokens", capturedUrl)
        }

    @Test
    fun tokenizeSendsAuthorizationHeader() =
        runTest {
            var capturedAuthHeader = ""

            val mockClient =
                HttpClient(MockEngine) {
                    install(ContentNegotiation) {
                        json(Json { ignoreUnknownKeys = true })
                    }
                    engine {
                        addHandler { request ->
                            capturedAuthHeader = request.headers["Authorization"] ?: ""
                            respond(
                                content =
                                    """{"id":"tok_auth","livemode":false,"used":false,"object":"token"}""",
                                status = HttpStatusCode.OK,
                                headers =
                                    headersOf(
                                        HttpHeaders.ContentType,
                                        ContentType.Application.Json.toString(),
                                    ),
                            )
                        }
                    }
                }

            val service =
                TokenizerApiService(
                    config = testConfig,
                    httpClient = mockClient,
                    cryptoService = fakeEncryptor,
                )

            service.tokenize(
                cardNumber = "4242424242424242",
                expMonth = "12",
                expYear = "26",
                cvc = "123",
                cardholderName = "Test",
            )

            assertTrue(capturedAuthHeader.startsWith("Basic "), "Auth header should start with 'Basic '")
            assertTrue(capturedAuthHeader.length > 7, "Auth header should contain encoded value")
        }

    @Test
    fun tokenizeEncryptionFailureReturnsNetworkError() =
        runTest {
            val failingEncryptor =
                object : CardEncryptor {
                    override fun encryptCardData(
                        cardJson: String,
                        rsaPublicKeyBase64: String,
                    ): EncryptedCardData = throw RuntimeException("Encryption failed")
                }

            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.OK,
                    responseBody =
                        """{"id":"tok_never","livemode":false,"used":false,"object":"token"}""",
                )

            val service =
                TokenizerApiService(
                    config = testConfig,
                    httpClient = mockClient,
                    cryptoService = failingEncryptor,
                )

            val result =
                service.tokenize(
                    cardNumber = "4242424242424242",
                    expMonth = "12",
                    expYear = "26",
                    cvc = "123",
                    cardholderName = "Test",
                )

            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertIs<TokenizerApiException>(exception)
            val networkError = assertIs<TokenizerError.NetworkError>(exception.tokenizerError)
            assertTrue(
                networkError.message.contains("Encryption failed"),
            )
        }

    @Test
    fun tokenizeAmexLastFourExtractsCorrectly() =
        runTest {
            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.OK,
                    responseBody =
                        """{"id":"tok_amex","livemode":false,"used":false,"object":"token"}""",
                )

            val service =
                TokenizerApiService(
                    config = testConfig,
                    httpClient = mockClient,
                    cryptoService = fakeEncryptor,
                )

            val result =
                service.tokenize(
                    cardNumber = "378282246310005",
                    expMonth = "12",
                    expYear = "26",
                    cvc = "1234",
                    cardholderName = "Test",
                )

            assertTrue(result.isSuccess)
            assertEquals("0005", result.getOrThrow().lastFour)
        }
}
