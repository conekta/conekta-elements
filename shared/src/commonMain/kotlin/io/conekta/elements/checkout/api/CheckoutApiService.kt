package io.conekta.elements.checkout.api

import io.conekta.elements.checkout.models.CheckoutConfig
import io.conekta.elements.checkout.models.CheckoutError
import io.conekta.elements.checkout.models.CheckoutOrderResult
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.localization.normalizeConektaLanguageTag
import io.conekta.elements.network.ConektaHttpClient
import io.conekta.elements.network.HEADER_ACCEPT_CONEKTA_VERSION
import io.conekta.elements.network.HEADER_CONEKTA_CLIENT_USER_AGENT
import io.conekta.elements.network.sdkUserAgent
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess

open class CheckoutApiService(
    private val config: CheckoutConfig,
    private val httpClient: HttpClient = ConektaHttpClient.create(),
) {
    private val json = ConektaHttpClient.json

    open suspend fun fetchCheckout(): Result<CheckoutResult> =
        try {
            val url = "${config.baseUrl}/checkout-bff/v1/checkout-requests/${config.checkoutRequestId}"
            val requestHeaders = commonHeaders()

            val response =
                httpClient.get(url) {
                    headers {
                        requestHeaders.forEach { (name, value) -> set(name, value) }
                    }
                }

            if (response.status.isSuccess()) {
                val body = response.bodyAsText()
                parseCheckoutResult(body)
            } else {
                buildHttpErrorResult(response.status.value, response.bodyAsText())
            }
        } catch (e: CheckoutApiException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(
                CheckoutApiException(
                    CheckoutError.NetworkError(e.asNetworkErrorMessage("Unknown network error")),
                ),
            )
        }

    open suspend fun createOrder(
        paymentMethod: String,
        tokenId: String? = null,
    ): Result<CheckoutOrderResult> =
        try {
            val apiValue =
                CheckoutPaymentMethods.toApiValue(paymentMethod)
                    ?: return Result.failure(
                        CheckoutApiException(
                            CheckoutError.ValidationError("Unsupported payment method: $paymentMethod"),
                        ),
                    )

            val requestBody =
                CreateOrderRequestDto(
                    checkoutRequestId = config.checkoutRequestId,
                    paymentMethod = apiValue,
                    tokenId = tokenId,
                )
            val url = "${config.baseUrl}/checkout-bff/v2/order"
            val requestHeaders = commonHeaders()

            val response =
                httpClient.post(url) {
                    contentType(ContentType.Application.Json)
                    headers {
                        requestHeaders.forEach { (name, value) -> set(name, value) }
                    }
                    setBody(json.encodeToString(CreateOrderRequestDto.serializer(), requestBody))
                }

            if (response.status.isSuccess()) {
                val body = response.bodyAsText()
                val dto = json.decodeFromString(CreateOrderResponseDto.serializer(), body)
                Result.success(dto.toDomain())
            } else {
                buildHttpErrorResult(response.status.value, response.bodyAsText())
            }
        } catch (e: CheckoutApiException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(
                CheckoutApiException(
                    CheckoutError.NetworkError(e.asNetworkErrorMessage("Unknown network error")),
                ),
            )
        }

    private fun parseCheckoutResult(body: String): Result<CheckoutResult> {
        val checkoutRequestDto = decodeOrNull(CheckoutRequestResponseDto.serializer(), body)

        if (checkoutRequestDto != null) {
            return Result.success(checkoutRequestDto.toDomain())
        }

        val checkoutOrderDto = decodeOrNull(CheckoutOrderResponseDto.serializer(), body)

        return if (checkoutOrderDto != null) {
            Result.success(checkoutOrderDto.toDomain())
        } else {
            Result.failure(
                CheckoutApiException(
                    CheckoutError.NetworkError("Could not parse checkout response"),
                ),
            )
        }
    }

    private fun buildHttpErrorResult(
        statusCode: Int,
        errorBody: String,
    ): Result<Nothing> {
        val errorResponse =
            try {
                json.decodeFromString(CheckoutErrorResponseDto.serializer(), errorBody)
            } catch (_: Exception) {
                null
            }

        return if (errorResponse != null) {
            Result.failure(
                CheckoutApiException(
                    CheckoutError.ApiError(
                        code = errorResponse.type,
                        message =
                            errorResponse.messageToPurchaser.ifEmpty {
                                errorResponse.message.ifEmpty { errorBody }
                            },
                    ),
                ),
            )
        } else {
            Result.failure(
                CheckoutApiException(
                    CheckoutError.NetworkError("HTTP $statusCode: $errorBody"),
                ),
            )
        }
    }

    private fun <T> decodeOrNull(
        serializer: kotlinx.serialization.KSerializer<T>,
        body: String,
    ): T? =
        try {
            json.decodeFromString(serializer, body)
        } catch (_: Exception) {
            null
        }

    private fun commonHeaders(): Map<String, String> =
        mapOf(
            HttpHeaders.Authorization to "Bearer ${config.publicKey}",
            "x-jwt-token" to config.jwtToken,
            HttpHeaders.AcceptLanguage to normalizeConektaLanguageTag(config.languageTag),
            HttpHeaders.Accept to HEADER_ACCEPT_CONEKTA_VERSION,
            HEADER_CONEKTA_CLIENT_USER_AGENT to sdkUserAgent,
        )

    private fun Throwable.asNetworkErrorMessage(defaultMessage: String): String {
        val type = this::class.simpleName ?: "Exception"
        val directMessage = message?.trim().orEmpty()
        val causeMessage = cause?.message?.trim().orEmpty()

        return when {
            directMessage.isNotEmpty() -> "$type: $directMessage"
            causeMessage.isNotEmpty() -> "$type (cause: $causeMessage)"
            else -> "$type: $defaultMessage"
        }
    }
}

class CheckoutApiException(
    val checkoutError: CheckoutError,
) : Exception(
        when (checkoutError) {
            is CheckoutError.ApiError -> checkoutError.message
            is CheckoutError.NetworkError -> checkoutError.message
            is CheckoutError.ValidationError -> checkoutError.message
        },
    )
