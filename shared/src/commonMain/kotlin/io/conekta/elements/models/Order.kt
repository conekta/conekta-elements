package io.conekta.elements.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class CreateOrderPayload(
    val checkoutRequestId: String,
    val paymentMethod: String,
    val fingerprint: String? = null,
    val customerInfo: CustomerInfo? = null,
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

@Serializable
data class OrderResponse(
    val id: String = "",
    val reference: String = "",
    val status: String = "",
    @SerialName("urlRedirect")
    val urlRedirect: String = "",
    val charges: List<JsonElement> = emptyList(),
    @SerialName("metaData")
    val metaData: JsonElement? = null,
    @SerialName("nextAction")
    val nextAction: NextAction? = null,
    @SerialName("paymentStatus")
    val paymentStatus: String? = null,
    val errors: List<JsonElement>? = null,
)

@Serializable
data class NextAction(
    val type: String? = null,
    @SerialName("redirectToUrl")
    val redirectToUrl: RedirectToUrl,
)

@Serializable
data class RedirectToUrl(
    val returnUrl: String? = null,
    val url: String,
)
