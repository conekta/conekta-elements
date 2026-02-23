package io.conekta.elements.checkout.api

import io.conekta.elements.checkout.models.CheckoutAmountLine
import io.conekta.elements.checkout.models.CheckoutConfig
import io.conekta.elements.checkout.models.CheckoutError
import io.conekta.elements.checkout.models.CheckoutLineItem
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.checkout.models.CheckoutProvider
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.localization.normalizeConektaLanguageTag
import io.conekta.elements.network.ConektaHttpClient
import io.conekta.elements.network.sdkUserAgent
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess

open class CheckoutApiService(
    private val config: CheckoutConfig,
    private val httpClient: HttpClient = ConektaHttpClient.create(),
) {
    private val json = ConektaHttpClient.json

    open suspend fun fetchCheckout(): Result<CheckoutResult> =
        try {
            val response =
                httpClient.get("${baseUrl()}checkout-requests/${config.checkoutRequestId}") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer ${config.publicKey}")
                        append("x-jwt-token", config.jwtToken)
                        append(HttpHeaders.AcceptLanguage, normalizeConektaLanguageTag(config.languageTag))
                        append(HttpHeaders.Accept, "application/vnd.conekta-v2.2.0+json")
                        append("Conekta-Client-User-Agent", sdkUserAgent)
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

    private fun baseUrl(): String = if (config.baseUrl.endsWith('/')) config.baseUrl else "${config.baseUrl}/"
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
