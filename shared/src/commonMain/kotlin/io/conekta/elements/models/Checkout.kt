package io.conekta.elements.models

import io.conekta.elements.orchestrator.PaymentMethodType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Checkout(
    val id: String,
    @SerialName("entityId")
    val entityId: String = "",
    @SerialName("companyId")
    val companyId: String = "",
    val name: String = "",
    val amount: Long = 0,
    val quantity: Int = 0,
    @SerialName("liveMode")
    val liveMode: Boolean = false,
    val status: CheckoutStatus = CheckoutStatus.PENDING,
    val type: String = "",
    val recurrent: Boolean = false,

    // epoch time
    @SerialName("expiredAt")
    val expiredAt: Long = 0,
    @SerialName("startsAt")
    val startsAt: Long = 0,

    @SerialName("allowedPaymentMethods")
    val allowedPaymentMethods: List<PaymentMethodType> = emptyList(),

    val slug: String = "",
    val url: String = "",

    @SerialName("needsShippingContact")
    val needsShippingContact: Boolean = false,

    @SerialName("orderTemplate")
    val orderTemplate: OrderTemplate = OrderTemplate(),

    val orders: List<JsonElement> = emptyList(),

    @SerialName("monthlyInstallmentsEnabled")
    val monthlyInstallmentsEnabled: Boolean = false,
    @SerialName("monthlyInstallmentsOptions")
    val monthlyInstallmentsOptions: List<Int> = emptyList(),

    @SerialName("force3dsFlow")
    val force3dsFlow: Boolean = false,

    @SerialName("excludeCardNetworks")
    val excludeCardNetworks: List<String> = emptyList(),

    @SerialName("canNotExpire")
    val canNotExpire: Boolean = false,

    @SerialName("redirectionTime")
    val redirectionTime: Int = 0,

    val providers: List<Provider> = emptyList(),

    @SerialName("femsaMigrated")
    val femsaMigrated: Boolean = false,

    @SerialName("threeDs")
    val threeDs: ThreeDsValues? = null,

    @SerialName("maxFailedRetries")
    val maxFailedRetries: Int? = null,

    @SerialName("failureUrl")
    val failureUrl: String? = null,
    @SerialName("successUrl")
    val successUrl: String? = null,

    @SerialName("plans")
    val plans: List<Plan>? = null,
)

@Serializable
enum class CheckoutStatus {
    @SerialName("Canceled")
    CANCELED,

    @SerialName("Cancelled")
    CANCELLED,

    @SerialName("Expired")
    EXPIRED,

    @SerialName("Finalized")
    FINALIZED,

    @SerialName("Paid")
    PAID,

    @SerialName("Pending_payment")
    PENDING,

    @SerialName("Issued")
    ISSUED,
}

@Serializable
enum class ThreeDsValues {
    @SerialName("Enabled")
    ENABLE,

    @SerialName("EnabledDynamic")
    DYNAMIC,

    @SerialName("NotValid")
    NOT,

    @SerialName("Off")
    OFF,
}

@Serializable
data class Provider(
    val id: String,
    val name: String = "",
    val paymentMethod: String = "",
    val haveAccount: Boolean? = null,
)

@Serializable
data class OrderTemplate(
    val lineItems: List<JsonElement> = emptyList(),
    val customerInfo: CustomerInfo? = null,
    val currency: String = "MXN",
    val metadata: List<JsonElement>? = null,
    val shippingLines: List<JsonElement>? = null,
    val taxLines: List<JsonElement>? = null,
    val discountLines: List<JsonElement>? = null,
    val subtotal: Long? = null,
)

@Serializable
data class CustomerInfo(
    val corporate: Boolean? = null,
    val customerFingerprint: String? = null,
    val customerId: String? = null,
    val email: String = "",
    val name: String = "",
    val phone: String = "",
)

@Serializable
data class Plan(
    val id: String,
    val name: String = "",
    val amount: Long = 0,
    val currency: String = "MXN",
    val interval: SubscriptionInterval = SubscriptionInterval.month,
    val frequency: Int = 0,
    val expiryCount: Int = 0,
    val subscriptionStart: Long = 0,
    val subscriptionEnd: Long = 0,
    val trialStart: Long = 0,
    val trialEnd: Long = 0,
    val trialPeriodDays: Int = 0,
    val liveMode: Boolean = false,
    val createdAt: Long = 0,
)

@Serializable
enum class SubscriptionInterval {
    day,
    week,
    month,
    year,
    half_month,
    minute
  }
  