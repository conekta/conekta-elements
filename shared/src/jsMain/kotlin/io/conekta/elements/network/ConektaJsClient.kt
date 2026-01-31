package io.conekta.elements.network

import io.conekta.elements.network.models.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.promise
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.Promise

@OptIn(ExperimentalJsExport::class)
@JsExport
class ConektaJsClient(
    apiKey: String,
    language: String = ConektaConfig.DEFAULT_LANGUAGE,
    apiVersion: String = ConektaConfig.DEFAULT_API_VERSION,
) {
    private val scope = CoroutineScope(SupervisorJob())
    private val service =
        ConektaApiService(
            ConektaConfig(
                apiKey = apiKey,
                language = language,
                apiVersion = apiVersion,
            ),
        )

    fun getOrder(orderId: String): Promise<Order> =
        scope.promise {
            when (val result = service.getOrder(orderId)) {
                is ApiResult.Success -> result.data
                is ApiResult.Error -> throw Exception(result.message)
                is ApiResult.Exception -> throw result.throwable
            }
        }

    fun close() {
        service.close()
    }
}
