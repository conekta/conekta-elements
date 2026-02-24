package io.conekta.elements.checkout.api

import io.conekta.elements.checkout.models.CheckoutAmountLine
import io.conekta.elements.checkout.models.CheckoutConfig
import io.conekta.elements.checkout.models.CheckoutError
import io.conekta.elements.checkout.models.CheckoutLineItem
import io.conekta.elements.checkout.models.CheckoutOrderResult
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.checkout.models.CheckoutProvider
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.localization.normalizeConektaLanguageTag
import io.conekta.elements.network.ConektaHttpClient
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
            val url = "${baseUrl()}checkout-requests/${config.checkoutRequestId}"
            val requestHeaders = commonHeaders()
            logRequestHeaders(method = "GET", url = url, headers = requestHeaders)

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
                val errorBody = response.bodyAsText()
                val errorResponse =
                    try {
                        json.decodeFromString(CheckoutErrorResponseDto.serializer(), errorBody)
                    } catch (_: Exception) {
                        null
                    }

                if (errorResponse != null) {
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
                            CheckoutError.NetworkError("HTTP ${response.status.value}: $errorBody"),
                        ),
                    )
                }
            }
        } catch (e: CheckoutApiException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(
                CheckoutApiException(
                    CheckoutError.NetworkError(e.message ?: "Unknown network error"),
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
            val url = "${baseOrigin()}/checkout-bff/v2/order"
            val requestHeaders = commonHeaders()
            logRequestHeaders(method = "POST", url = url, headers = requestHeaders)

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
                Result.success(CheckoutOrderResult(orderId = dto.id))
            } else {
                val errorBody = response.bodyAsText()
                val errorResponse =
                    try {
                        json.decodeFromString(CheckoutErrorResponseDto.serializer(), errorBody)
                    } catch (_: Exception) {
                        null
                    }

                if (errorResponse != null) {
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
                            CheckoutError.NetworkError("HTTP ${response.status.value}: $errorBody"),
                        ),
                    )
                }
            }
        } catch (e: CheckoutApiException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(
                CheckoutApiException(
                    CheckoutError.NetworkError(e.message ?: "Unknown network error"),
                ),
            )
        }

    private fun parseCheckoutResult(body: String): Result<CheckoutResult> {
        val checkoutRequestDto =
            if (isCheckoutRequestResponse(body)) {
                try {
                    json.decodeFromString(CheckoutRequestResponseDto.serializer(), body)
                } catch (_: Exception) {
                    null
                }
            } else {
                null
            }

        if (checkoutRequestDto != null) {
            return Result.success(
                CheckoutResult(
                    orderId = checkoutRequestDto.id,
                    checkoutId = checkoutRequestDto.id,
                    amount = checkoutRequestDto.amount,
                    currency = checkoutRequestDto.orderTemplate?.currency ?: "MXN",
                    allowedPaymentMethods =
                        checkoutRequestDto.allowedPaymentMethods.map { normalizePaymentMethodValue(it) },
                    providers =
                        checkoutRequestDto.providers.map {
                            CheckoutProvider(
                                id = it.id,
                                name = it.name,
                                paymentMethod = normalizePaymentMethodValue(it.paymentMethod),
                            )
                        },
                    lineItems =
                        checkoutRequestDto.orderTemplate
                            ?.lineItems
                            .orEmpty()
                            .map {
                                CheckoutLineItem(
                                    name = it.name,
                                    quantity = it.quantity,
                                    unitPrice = it.unitPrice,
                                )
                            },
                    taxLines =
                        checkoutRequestDto.orderTemplate
                            ?.taxLines
                            .orEmpty()
                            .map { CheckoutAmountLine(description = it.description.orEmpty(), amount = it.amount) },
                    discountLines =
                        checkoutRequestDto.orderTemplate
                            ?.discountLines
                            .orEmpty()
                            .map { CheckoutAmountLine(description = it.description.orEmpty(), amount = it.amount) },
                    shippingLines =
                        checkoutRequestDto.orderTemplate
                            ?.shippingLines
                            .orEmpty()
                            .map { CheckoutAmountLine(description = it.description.orEmpty(), amount = it.amount) },
                ),
            )
        }

        val checkoutOrderDto =
            try {
                json.decodeFromString(CheckoutOrderResponseDto.serializer(), body)
            } catch (_: Exception) {
                null
            }

        return if (checkoutOrderDto != null) {
            Result.success(
                CheckoutResult(
                    orderId = checkoutOrderDto.id,
                    checkoutId = checkoutOrderDto.checkout.id,
                    amount = checkoutOrderDto.amount,
                    currency = checkoutOrderDto.currency,
                    allowedPaymentMethods =
                        checkoutOrderDto.checkout.allowedPaymentMethods.map { normalizePaymentMethodValue(it) },
                    providers = emptyList(),
                    lineItems =
                        checkoutOrderDto.lineItems
                            ?.data
                            .orEmpty()
                            .map {
                                CheckoutLineItem(
                                    name = it.name,
                                    quantity = it.quantity,
                                    unitPrice = it.unitPrice,
                                )
                            },
                    taxLines =
                        checkoutOrderDto.taxLines
                            ?.data
                            .orEmpty()
                            .map { CheckoutAmountLine(description = it.description.orEmpty(), amount = it.amount) },
                    discountLines =
                        checkoutOrderDto.discountLines
                            ?.data
                            .orEmpty()
                            .map { CheckoutAmountLine(description = it.description.orEmpty(), amount = it.amount) },
                    shippingLines =
                        checkoutOrderDto.shippingLines
                            ?.data
                            .orEmpty()
                            .map { CheckoutAmountLine(description = it.description.orEmpty(), amount = it.amount) },
                ),
            )
        } else {
            Result.failure(
                CheckoutApiException(
                    CheckoutError.NetworkError("Could not parse checkout response"),
                ),
            )
        }
    }

    private fun normalizePaymentMethodValue(value: String): String {
        val compact =
            value
                .trim()
                .replace(" ", "")
                .replace("_", "")
                .lowercase()

        return when (compact) {
            "card" -> CheckoutPaymentMethods.CARD
            "cash" -> CheckoutPaymentMethods.CASH
            "banktransfer" -> CheckoutPaymentMethods.BANK_TRANSFER
            "paybybank" -> "pay_by_bank"
            else -> value.trim().lowercase()
        }
    }

    private fun isCheckoutRequestResponse(body: String): Boolean =
        body.contains("\"allowedPaymentMethods\"") || body.contains("\"orderTemplate\"")

    private fun commonHeaders(): Map<String, String> =
        mapOf(
            HttpHeaders.Authorization to "Bearer ${config.publicKey}",
            "x-jwt-token" to config.jwtToken,
            HttpHeaders.AcceptLanguage to normalizeConektaLanguageTag(config.languageTag),
            HttpHeaders.Accept to "application/vnd.conekta-v2.2.0+json",
            HEADER_CONEKTA_CLIENT_USER_AGENT to sdkUserAgent,
        )

    private fun logRequestHeaders(
        method: String,
        url: String,
        headers: Map<String, String>,
    ) {
        val payload =
            headers.entries.joinToString(separator = ", ") { (key, value) ->
                "$key=${maskHeaderValue(key, value)}"
            }
        println("ConektaCheckoutApi [$method] $url headers: $payload")
    }

    private fun maskHeaderValue(
        headerName: String,
        headerValue: String,
    ): String =
        when (headerName.lowercase()) {
            "authorization",
            "x-jwt-token",
            -> {
                if (headerValue.length <= 10) {
                    "***"
                } else {
                    "${headerValue.take(6)}***${headerValue.takeLast(4)}"
                }
            }
            else -> headerValue
        }

    private fun baseUrl(): String = if (config.baseUrl.endsWith('/')) config.baseUrl else "${config.baseUrl}/"

    private fun baseOrigin(): String {
        val url = config.baseUrl
        val schemeEnd = url.indexOf("://")
        if (schemeEnd == -1) return url
        val pathStart = url.indexOf('/', schemeEnd + 3)
        return if (pathStart == -1) url else url.substring(0, pathStart)
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
