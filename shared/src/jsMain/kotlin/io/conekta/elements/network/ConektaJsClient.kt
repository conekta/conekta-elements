package io.conekta.elements.network

import io.conekta.elements.dtos.CheckoutDto
import io.conekta.elements.dtos.FeatureFlagDto
import io.conekta.elements.mappers.toDto
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
) {
    private val scope = CoroutineScope(SupervisorJob())

        private val checkoutService =
        CheckoutSsrApiService(
            CheckoutSsrConfig(
                language = language,
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

    fun close() {
        checkoutService.close()
    }
}
