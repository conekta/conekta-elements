package io.conekta.elements.network

import io.conekta.elements.network.ConektaHttpClient
import io.conekta.elements.network.sdkUserAgent
import io.conekta.elements.models.Checkout
import io.ktor.client.call.body
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess

class CheckoutSsrApiService(
    private val config: CheckoutSsrConfig,
    private val httpClient: HttpClient = ConektaHttpClient.create(),
) {
    private fun normalizeBaseUrl(url: String): String = url.trimEnd('/') + "/"

    suspend fun getCheckoutById(id: String): Result<Checkout> =
        try {
            val base = normalizeBaseUrl(config.baseUrl)
            val url = "${base}api/checkout/$id"

            val response = httpClient.get(url) {
                headers {
                    append(HttpHeaders.AcceptLanguage, config.language)
                    append("x-source", config.source)
                }
            }

            if (response.status.isSuccess()) {
                val checkout: Checkout = response.body()
                Result.success(checkout)
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(
                    CheckoutSsrException(
                        CheckoutSsrError.HttpError(
                            status = response.status.value,
                            body = errorBody,
                        ),
                    ),
                )
            }
        } catch (e: CheckoutSsrException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(
                CheckoutSsrException(
                    CheckoutSsrError.NetworkError(e.message ?: "Unknown network error"),
                ),
            )
        }

    fun close() {
        httpClient.close()
    }
}

sealed class CheckoutSsrError(message: String) : Throwable(message) {
    data class HttpError(val status: Int, val body: String) :
        CheckoutSsrError("HTTP $status: $body")

    data class NetworkError(val details: String) :
        CheckoutSsrError(details)
}

class CheckoutSsrException(
    val checkoutError: CheckoutSsrError,
) : Exception(checkoutError.message)
