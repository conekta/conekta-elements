package io.conekta.elements.checkout.api

import io.conekta.elements.checkout.models.CurrencyCodes
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
data class CheckoutRequestResponseDto(
    val id: String,
    val name: String,
    val amount: Long,
    val status: String? = null,
    @SerialName("allowedPaymentMethods") val allowedPaymentMethods: List<String> = emptyList(),
    @SerialName("orderTemplate") val orderTemplate: CheckoutOrderTemplateDto = CheckoutOrderTemplateDto(),
    val providers: List<CheckoutProviderDto> = emptyList(),
    @SerialName("startsAt") val startsAt: Long? = null,
)

@Serializable
data class CheckoutOrderTemplateDto(
    val currency: String = CurrencyCodes.MXN,
    @SerialName("lineItems") val lineItems: List<CheckoutLineItemTemplateDto> = emptyList(),
    @SerialName("taxLines") val taxLines: List<CheckoutAmountLineTemplateDto> = emptyList(),
    @SerialName("discountLines") val discountLines: List<CheckoutAmountLineTemplateDto> = emptyList(),
    @SerialName("shippingLines") val shippingLines: List<CheckoutAmountLineTemplateDto> = emptyList(),
    @SerialName("customerInfo") val customerInfo: CheckoutCustomerInfoDto? = null,
)

@Serializable
data class CheckoutCustomerInfoDto(
    val email: String = "",
)

@Serializable
data class CheckoutLineItemTemplateDto(
    val name: String = "",
    val quantity: Long = 0,
    @SerialName("unitPrice") val unitPrice: Long = 0,
)

@Serializable
data class CheckoutAmountLineTemplateDto(
    val description: String? = null,
    val amount: Long = 0,
)

@Serializable
data class CheckoutProviderDto(
    val id: String = "",
    val name: String = "",
    @SerialName("paymentMethod") val paymentMethod: String = "",
    @SerialName("product_type") val productType: String = "",
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

@Serializable
data class CreateOrderRequestDto(
    val checkoutRequestId: String,
    val paymentMethod: String,
    val tokenId: String? = null,
)

@Serializable
data class CreateOrderResponseDto(
    val id: String,
    val status: String = "",
    val charges: List<CreateOrderChargeDto> = emptyList(),
)

@Serializable
data class CreateOrderChargeDto(
    val amount: Long = 0,
    val currency: String = CurrencyCodes.MXN,
    val status: String = "",
    @SerialName("payment_method") val paymentMethod: CreateOrderChargePaymentMethodDto? = null,
)

@Serializable
data class CreateOrderChargePaymentMethodDto(
    val type: String? = null,
    val reference: String? = null,
    val clabe: String? = null,
    @SerialName("barcodeUrl") val barcodeUrl: String? = null,
    @SerialName("expiresAt") val expiresAt: Long = 0,
    @SerialName("service_name") val serviceName: String? = null,
    @SerialName("store_name") val storeName: String? = null,
    val provider: String? = null,
    val agreement: String? = null,
    val name: String? = null,
    @SerialName("product_type") val productType: String? = null,
)
