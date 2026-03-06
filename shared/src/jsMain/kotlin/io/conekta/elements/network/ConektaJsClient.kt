package io.conekta.elements.network

import io.conekta.elements.dtos.CheckoutDto
import io.conekta.elements.dtos.CreateOrderPayloadDto
import io.conekta.elements.dtos.FeatureFlagDto
import io.conekta.elements.dtos.OrderResponseDto
import io.conekta.elements.mappers.toDto
import io.conekta.elements.mappers.toModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.promise
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.Promise

@OptIn(ExperimentalJsExport::class)
@JsExport
class ConektaJsClient(
    language: String = CheckoutSsrConfig.DEFAULT_LANGUAGE,
    baseUrl: String = CheckoutSsrConfig.DEFAULT_BASE_URL,
) {
    private val scope = CoroutineScope(SupervisorJob())

    private val checkoutService =
        CheckoutSsrApiService(
            CheckoutSsrConfig(
                language = language,
                baseUrl = baseUrl,
            ),
        )

    fun getCheckoutById(id: String): Promise<CheckoutDto> =
        scope.promise {
            val checkout =
                checkoutService
                    .getCheckoutById(id)
                    .getOrElse { throw it }

            checkout.toDto()
        }

    fun getFeatureFlagByName(appId: String, flagName: String): Promise<FeatureFlagDto> =
        scope.promise {
            val featureFlag =
                checkoutService
                    .getFeatureFlagByName(appId, flagName)
                    .getOrElse { throw it }

            featureFlag.toDto()
        }

    fun createOrder(payload: CreateOrderPayloadDto): Promise<OrderResponseDto> =
        scope.promise {
            val order =
                checkoutService
                    .createOrder(payload.toModel())
                    .getOrElse { throw it }

            order.toDto()
        }

    fun close() {
        checkoutService.close()
    }
}
