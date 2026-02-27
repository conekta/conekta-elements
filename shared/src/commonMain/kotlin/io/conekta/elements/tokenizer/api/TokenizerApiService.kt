package io.conekta.elements.tokenizer.api

import io.conekta.elements.localization.normalizeConektaLanguageTag
import io.conekta.elements.network.ConektaHttpClient
import io.conekta.elements.network.HEADER_ACCEPT_CONEKTA_VERSION
import io.conekta.elements.network.HEADER_CONEKTA_CLIENT_USER_AGENT
import io.conekta.elements.network.sdkUserAgent
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
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess

/**
 * Service that orchestrates card data encryption and tokenization via the Conekta API.
 *
 * Flow:
 * 1. Encrypt each card field individually with RSA PKCS#1 v1.5 (mirrors conekta-ios Card.m)
 * 2. POST {"card": {encrypted fields}} to {baseUrl}/tokens with Bearer auth
 * 3. Parse response
 */
class TokenizerApiService(
    private val config: TokenizerConfig,
    private val languageTag: String? = null,
    private val httpClient: HttpClient = ConektaHttpClient.create(),
    private val cryptoService: CardEncryptor = CryptoService(),
) {
    private val json = ConektaHttpClient.json

    suspend fun tokenize(
        cardNumber: String,
        expMonth: String,
        expYear: String,
        cvc: String,
        cardholderName: String,
    ): Result<TokenResult> =
        try {
            // 1. Encrypt each field individually (same as conekta-ios Card.m setNumber:name:cvc:expMonth:expYear:)
            val requestBody =
                CardPayloadDto(
                    card =
                        CardDataDto(
                            number = cryptoService.encrypt(cardNumber, config.rsaPublicKey),
                            name = cryptoService.encrypt(cardholderName, config.rsaPublicKey),
                            cvc = cryptoService.encrypt(cvc, config.rsaPublicKey),
                            expMonth = cryptoService.encrypt(expMonth, config.rsaPublicKey),
                            expYear = cryptoService.encrypt(expYear, config.rsaPublicKey),
                        ),
                )

            // 2. POST to API
            val url = "${config.baseUrl}/tokens"
            val response =
                httpClient.post(url) {
                    contentType(ContentType.Application.Json)
                    headers {
                        set(HttpHeaders.Authorization, "Bearer ${config.publicKey}")
                        set(HttpHeaders.AcceptLanguage, normalizeConektaLanguageTag(languageTag))
                        set(HttpHeaders.Accept, HEADER_ACCEPT_CONEKTA_VERSION)
                        set(HEADER_CONEKTA_CLIENT_USER_AGENT, sdkUserAgent)
                    }
                    setBody(requestBody)
                }

            // 3. Parse response
            if (response.status.isSuccess()) {
                val tokenResponse: TokenResponseDto = response.body()
                val lastFour = cardNumber.filter { it.isDigit() }.takeLast(4)
                Result.success(
                    TokenResult(
                        token = tokenResponse.id,
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
                    val apiMessage =
                        errorResponse.messageToPurchaser
                            .ifEmpty { errorResponse.message }
                            .ifEmpty {
                                errorResponse.details
                                    .firstOrNull()
                                    ?.message
                                    .orEmpty()
                            }.ifEmpty { "Unknown API error" }
                    Result.failure(
                        TokenizerApiException(
                            TokenizerError.ApiError(
                                code = errorResponse.type,
                                message = apiMessage,
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
