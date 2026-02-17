package io.conekta.elements.tokenizer.api

import io.conekta.elements.network.ConektaHttpClient
import io.conekta.elements.tokenizer.crypto.CardEncryptor
import io.conekta.elements.tokenizer.crypto.CryptoService
import io.conekta.elements.tokenizer.models.TokenResult
import io.conekta.elements.tokenizer.models.TokenizerConfig
import io.conekta.elements.tokenizer.models.TokenizerError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

/**
 * Service that orchestrates card data encryption and tokenization via the Conekta API.
 *
 * Flow:
 * 1. Build card payload → JSON serialize
 * 2. Encrypt with AES + RSA (CryptoJS-compatible)
 * 3. POST to {baseUrl}/tokens with Bearer auth
 * 4. Parse response
 */
class TokenizerApiService(
    private val config: TokenizerConfig,
    private val httpClient: HttpClient = ConektaHttpClient.create(),
    private val cryptoService: CardEncryptor = CryptoService(),
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun tokenize(
        cardNumber: String,
        expMonth: String,
        expYear: String,
        cvc: String,
        cardholderName: String,
    ): Result<TokenResult> =
        try {
            // 1. Build card payload
            val cardPayload =
                CardPayloadDto(
                    card =
                        CardDataDto(
                            cvc = cvc,
                            expMonth = expMonth,
                            expYear = expYear,
                            name = cardholderName,
                            number = cardNumber,
                        ),
                )

            val cardJson = json.encodeToString(CardPayloadDto.serializer(), cardPayload)

            // 2. Encrypt
            val encrypted = cryptoService.encryptCardData(cardJson, config.rsaPublicKey)
            val requestBody =
                TokenRequestDto(
                    data = encrypted.encryptedData,
                    key = encrypted.encryptedKey,
                )

            // 3. POST to API
            val url = "${config.baseUrl}tokens"
            val response =
                httpClient.post(url) {
                    contentType(ContentType.Application.Json)
                    headers {
                        append("Authorization", "Bearer ${config.publicKey}")
                        append("Accept", "application/vnd.conekta-v2.2.0+json")
                        append("Conekta-Client-User-Agent", """{"agent":"Conekta Elements SDK"}""")
                    }
                    setBody(requestBody)
                }

            // 5. Parse response
            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                val tokenResponse: TokenResponseDto = response.body()
                val lastFour = cardNumber.filter { it.isDigit() }.takeLast(4)
                Result.success(
                    TokenResult(
                        token = tokenResponse.id,
                        cardBrand = "",
                        lastFour = lastFour,
                    ),
                )
            } else {
                val errorBody = response.bodyAsText()
                val errorResponse =
                    try {
                        json.decodeFromString(TokenErrorResponseDto.serializer(), errorBody)
                    } catch (_: Exception) {
                        null
                    }

                if (errorResponse != null) {
                    Result.failure(
                        TokenizerApiException(
                            TokenizerError.ApiError(
                                code = errorResponse.type,
                                message = errorResponse.messageToPurchaser.ifEmpty { errorResponse.message },
                            ),
                        ),
                    )
                } else {
                    Result.failure(
                        TokenizerApiException(
                            TokenizerError.NetworkError("HTTP ${response.status.value}: $errorBody"),
                        ),
                    )
                }
            }
        } catch (e: TokenizerApiException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(
                TokenizerApiException(
                    TokenizerError.NetworkError(e.message ?: "Unknown network error"),
                ),
            )
        }
}

/**
 * Exception wrapper that carries a [TokenizerError] for structured error handling.
 */
class TokenizerApiException(
    val tokenizerError: TokenizerError,
) : Exception(
        when (tokenizerError) {
            is TokenizerError.ApiError -> tokenizerError.message
            is TokenizerError.NetworkError -> tokenizerError.message
            is TokenizerError.ValidationError -> tokenizerError.message
        },
    )
