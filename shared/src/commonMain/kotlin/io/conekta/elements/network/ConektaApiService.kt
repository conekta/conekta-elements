package io.conekta.elements.network

import io.conekta.elements.network.models.ConektaApiError
import io.conekta.elements.network.models.Order
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
sealed class ApiResult<out T> {
    data class Success<T>(
        val data: T,
    ) : ApiResult<T>()

    data class Error(
        val httpStatusCode: Int,
        val apiError: ConektaApiError? = null,
        val message: String = "",
    ) : ApiResult<Nothing>()

    data class Exception(
        val throwable: Throwable,
    ) : ApiResult<Nothing>()
}

class ConektaApiService(
    config: ConektaConfig,
    client: HttpClient? = null,
) {
    private val httpClient: HttpClient = client ?: createHttpClient(config)

    private val errorJson =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

    suspend fun getOrder(orderId: String): ApiResult<Order> =
        safeApiCall {
            httpClient.get("orders/$orderId")
        }

    private suspend inline fun <reified T> safeApiCall(crossinline block: suspend () -> HttpResponse): ApiResult<T> =
        try {
            val response = block()
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<T>())
            } else {
                val errorBody = response.bodyAsText()
                val apiError =
                    try {
                        errorJson.decodeFromString<ConektaApiError>(errorBody)
                    } catch (_: Throwable) {
                        null
                    }
                ApiResult.Error(
                    httpStatusCode = response.status.value,
                    apiError = apiError,
                    message = apiError?.details?.firstOrNull()?.message ?: errorBody,
                )
            }
        } catch (e: Throwable) {
            ApiResult.Exception(e)
        }

    fun close() {
        httpClient.close()
    }
}
