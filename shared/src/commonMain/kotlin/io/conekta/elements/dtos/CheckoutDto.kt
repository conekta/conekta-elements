package io.conekta.elements.dtos

import io.conekta.elements.orchestrator.PaymentMethodType
import io.conekta.elements.models.CheckoutStatus
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlin.js.JsExport

@JsExport
@Serializable
data class CheckoutDto(
    val id: String,
    val entityId: String,
    val companyId: String,
    val name: String,

    val amount: Long,
    val quantity: Int,
    val liveMode: Boolean,
    val status: CheckoutStatus,
    val type: String,
    val recurrent: Boolean,

    val expiredAt: Long,
    val startsAt: Long,

    // ✅ enum exportado (Opción A)
    val allowedPaymentMethods: Array<PaymentMethodType>,

    val slug: String,
    val url: String,

    val needsShippingContact: Boolean,

    val orderTemplate: OrderTemplateDto,

    val monthlyInstallmentsEnabled: Boolean,
    val monthlyInstallmentsOptions: Array<Int>,
    val force3dsFlow: Boolean,

    val excludeCardNetworks: Array<String>,
    val canNotExpire: Boolean,
    val redirectionTime: Int,

    val providers: Array<ProviderDto>,

    val femsaMigrated: Boolean,

    // si lo tienes en domain
    val threeDs: String?,

    val maxFailedRetries: Int?,
    val failureUrl: String?,
    val successUrl: String?,
)

@JsExport
@Serializable
data class ProviderDto(
    val id: String,
    val name: String,
    val paymentMethod: String,
    val haveAccount: Boolean?,
)

@JsExport
@Serializable
data class OrderTemplateDto(
    val lineItems: Array<JsonElement>,
    val customerInfo: CustomerInfoDto?,
    val currency: String,
    val metadata: Array<JsonElement>?,
    val shippingLines: Array<JsonElement>?,
    val taxLines: Array<JsonElement>?,
    val discountLines: Array<JsonElement>?,
    val subtotal: Long?,
)

@JsExport
@Serializable
data class CustomerInfoDto(
    val corporate: Boolean?,
    val customerFingerprint: String?,
    val customerId: String?,
    val email: String,
    val name: String,
    val phone: String,
)