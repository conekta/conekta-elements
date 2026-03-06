package io.conekta.elements.dtos

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlin.js.JsExport

@JsExport
@Serializable
data class CreateOrderPayloadDto(
    val checkoutRequestId: String,
    val paymentMethod: String,
    val fingerprint: String? = null,
    val customerInfo: CustomerInfoDto? = null,
    val shippingContact: JsonElement? = null,
    val paymentKey: String,
    val paymentSourceId: String,
    val fillPaymentFormTime: Double = 0.0,
    val checkoutAntifraudResponseID: String? = null,
    val savePaymentSource: Boolean? = null,
    val threeDsMode: String? = null,
    val returnUrl: String? = null,
    val planId: String? = null,
    val splitPayment: Boolean? = null,
    val originalOrderId: String? = null,
    val amount: Int? = null,
    val splitPaymentStep: Int? = null,
)

@JsExport
@Serializable
data class OrderResponseDto(
    val id: String,
    val reference: String,
    val status: String,
    val urlRedirect: String,
    val charges: Array<JsonElement>,
    val metaData: JsonElement?,
    val nextAction: NextActionDto?,
    val paymentStatus: String?,
    val errors: Array<JsonElement>?,
)

@JsExport
@Serializable
data class NextActionDto(
    val type: String?,
    val redirectToUrl: RedirectToUrlDto,
)

@JsExport
@Serializable
data class RedirectToUrlDto(
    val returnUrl: String?,
    val url: String,
)
