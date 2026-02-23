package io.conekta.elements.checkout.api

import io.conekta.elements.checkout.models.CheckoutAmountLine
import io.conekta.elements.checkout.models.CheckoutConfig
import io.conekta.elements.checkout.models.CheckoutError
import io.conekta.elements.checkout.models.CheckoutLineItem
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.network.ConektaHttpClient
import io.conekta.elements.network.sdkUserAgent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
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
                httpClient.get("${baseUrl()}checkouts/${config.checkoutRequestId}") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer ${config.publicKey}")
                        append("x-jwt-token", config.jwtToken)
                        append(HttpHeaders.Accept, "application/vnd.conekta-v2.2.0+json")
                        append("Conekta-Client-User-Agent", sdkUserAgent)
                    }
                }

            if (response.status.isSuccess()) {
                val dto: CheckoutOrderResponseDto = response.body()
                Result.success(
                    CheckoutResult(
                        orderId = dto.id,
                        checkoutId = dto.checkout.id,
                        amount = dto.amount,
                        currency = dto.currency,
                        allowedPaymentMethods = dto.checkout.allowedPaymentMethods,
                        lineItems =
                            dto.lineItems
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
                            dto.taxLines
                                ?.data
                                .orEmpty()
                                .map { CheckoutAmountLine(description = it.description.orEmpty(), amount = it.amount) },
                        discountLines =
                            dto.discountLines
                                ?.data
                                .orEmpty()
                                .map { CheckoutAmountLine(description = it.description.orEmpty(), amount = it.amount) },
                        shippingLines =
                            dto.shippingLines
                                ?.data
                                .orEmpty()
                                .map { CheckoutAmountLine(description = it.description.orEmpty(), amount = it.amount) },
                    ),
                )
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
