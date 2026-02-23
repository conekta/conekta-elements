package io.conekta.elements.checkout.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckoutOrderResponseDto(
    val id: String,
    val amount: Long,
    val currency: String,
    val checkout: CheckoutDetailsDto,
    @SerialName("line_items") val lineItems: CheckoutLineCollectionDto? = null,
    @SerialName("tax_lines") val taxLines: CheckoutAmountCollectionDto? = null,
    @SerialName("discount_lines") val discountLines: CheckoutAmountCollectionDto? = null,
    @SerialName("shipping_lines") val shippingLines: CheckoutAmountCollectionDto? = null,
)

@Serializable
data class CheckoutDetailsDto(
    val id: String,
    @SerialName("allowed_payment_methods") val allowedPaymentMethods: List<String> = emptyList(),
)

@Serializable
data class CheckoutLineCollectionDto(
    val data: List<CheckoutLineItemDto> = emptyList(),
)

@Serializable
data class CheckoutLineItemDto(
    val name: String = "",
    val quantity: Long = 0,
    @SerialName("unit_price") val unitPrice: Long = 0,
)

@Serializable
data class CheckoutAmountCollectionDto(
    val data: List<CheckoutAmountLineDto> = emptyList(),
)

@Serializable
data class CheckoutAmountLineDto(
    val description: String? = null,
    val amount: Long = 0,
)

@Serializable
data class CheckoutErrorResponseDto(
    @SerialName("object") val objectType: String = "error",
    val type: String = "",
    val message: String = "",
    @SerialName("message_to_purchaser") val messageToPurchaser: String = "",
)
