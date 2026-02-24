package io.conekta.elements.tokenizer.api

import io.conekta.elements.localization.ConektaLanguage
import io.conekta.elements.tokenizer.crypto.CardEncryptor
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
        onRequest: ((String?) -> Unit)? = null,
    ): HttpClient =
        HttpClient(MockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            engine {
                addHandler { request ->
                    onRequest?.invoke(request.headers[HttpHeaders.AcceptLanguage])
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

    @Test
    fun tokenizeSendsAcceptLanguageHeader() =
        runTest {
            var capturedAcceptLanguage: String? = null
            val mockClient =
                createMockClient(
                    statusCode = HttpStatusCode.OK,
                    responseBody = """{"id":"tok_lang","livemode":false,"used":false,"object":"token"}""",
                    onRequest = { acceptLanguage -> capturedAcceptLanguage = acceptLanguage },
                )

            val service =
                TokenizerApiService(
                    config = testConfig,
                    languageTag = ConektaLanguage.EN,
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

            assertTrue(result.isSuccess)
            assertEquals(ConektaLanguage.EN, capturedAcceptLanguage)
        }

    // Fake encryptor that prefixes each plaintext so we can verify it in the request body
    private val fakeEncryptor = CardEncryptor { plaintext, _ -> "enc:$plaintext" }

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
    fun tokenizePassesEachFieldToEncryptor() =
        runTest {
            val capturedPlaintexts = mutableListOf<String>()
            var capturedRsaKey = ""

            val capturingEncryptor =
                CardEncryptor { plaintext, rsaKey ->
                    capturedPlaintexts.add(plaintext)
                    capturedRsaKey = rsaKey
                    "encrypted"
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

            assertTrue(capturedPlaintexts.contains("4111111111111111"), "Card number should be encrypted")
            assertTrue(capturedPlaintexts.contains("03"), "Exp month should be encrypted")
            assertTrue(capturedPlaintexts.contains("29"), "Exp year should be encrypted")
            assertTrue(capturedPlaintexts.contains("456"), "CVC should be encrypted")
            assertTrue(capturedPlaintexts.contains("Jane Smith"), "Cardholder name should be encrypted")
            assertEquals("test_rsa_key", capturedRsaKey, "RSA key from config should be passed")
            assertEquals(5, capturedPlaintexts.size, "Exactly 5 fields should be encrypted")
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

            assertEquals("https://test.conekta.com/tokens", capturedUrl)
        }

    @Test
    fun tokenizeSendsRequiredHeaders() =
        runTest {
            var capturedAuthHeader = ""
            var capturedAcceptHeader = ""
            var capturedUserAgentHeader = ""

            val mockClient =
                HttpClient(MockEngine) {
                    install(ContentNegotiation) {
                        json(Json { ignoreUnknownKeys = true })
                    }
                    engine {
                        addHandler { request ->
                            capturedAuthHeader = request.headers["Authorization"] ?: ""
                            capturedAcceptHeader = request.headers["Accept"] ?: ""
                            capturedUserAgentHeader = request.headers["Conekta-Client-User-Agent"] ?: ""
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

            assertEquals(
                "Bearer key_test_abc123",
                capturedAuthHeader,
                "Auth header should be Bearer with public key",
            )
            assertEquals(
                "application/vnd.conekta-v2.2.0+json",
                capturedAcceptHeader,
                "Accept header should use Conekta API version",
            )
            assertTrue(
                capturedUserAgentHeader.contains("\"agent\"") && capturedUserAgentHeader.contains("Conekta"),
                "Conekta-Client-User-Agent header should contain agent JSON with Conekta identifier",
            )
        }

    @Test
    fun tokenizeEncryptionFailureReturnsNetworkError() =
        runTest {
            val failingEncryptor = CardEncryptor { _, _ -> throw RuntimeException("Encryption failed") }

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
            assertTrue(networkError.message.contains("Encryption failed"))
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
