package io.conekta.elements.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
@Serializable
data class Order(
    val id: String,
    @SerialName("livemode")
    val liveMode: Boolean = false,
    val amount: Long = 0,
    val currency: String = "MXN",
    @SerialName("payment_status")
    val paymentStatus: String = "",
    @SerialName("customer_info")
    val customerInfo: CustomerInfo? = null,
    val charges: ChargesResponse? = null,
)

@OptIn(ExperimentalJsExport::class)
@JsExport
@Serializable
data class CustomerInfo(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
)

@OptIn(ExperimentalJsExport::class)
@JsExport
@Serializable
data class ChargesResponse(
    @SerialName("has_more")
    val hasMore: Boolean = false,
    val total: Int = 0,
    val data: Array<Charge> = emptyArray(),
)

@OptIn(ExperimentalJsExport::class)
@JsExport
@Serializable
data class Charge(
    val id: String,
    val amount: Long = 0,
    val status: String = "",
    @SerialName("payment_method")
    val paymentMethod: PaymentMethod? = null,
)

@OptIn(ExperimentalJsExport::class)
@JsExport
@Serializable
data class PaymentMethod(
    val type: String = "",
    @SerialName("object")
    val objectType: String = "",
)
