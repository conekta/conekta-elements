@file:OptIn(kotlin.experimental.ExperimentalObjCName::class)

package io.conekta.elements.checkout.models

import io.conekta.elements.localization.ConektaLanguage
import kotlin.native.ObjCName

private const val PRODUCTION_BASE_URL = "https://api.conekta.io/"

@ObjCName("CheckoutConfig")
data class CheckoutConfig(
    val checkoutRequestId: String,
    val publicKey: String,
    val jwtToken: String,
    val merchantName: String = "Demo Store",
    val baseUrl: String = PRODUCTION_BASE_URL,
    val languageTag: String = ConektaLanguage.ES,
) {
    constructor(
        checkoutRequestId: String,
        publicKey: String,
        jwtToken: String,
    ) : this(
        checkoutRequestId = checkoutRequestId,
        publicKey = publicKey,
        jwtToken = jwtToken,
        merchantName = "Demo Store",
        baseUrl = PRODUCTION_BASE_URL,
        languageTag = ConektaLanguage.ES,
    )
}

@ObjCName("CheckoutResult")
data class CheckoutResult(
    val orderId: String,
    val checkoutId: String,
    val amount: Long,
    val currency: String,
    val allowedPaymentMethods: List<String>,
    val providers: List<CheckoutProvider> = emptyList(),
    val lineItems: List<CheckoutLineItem> = emptyList(),
    val taxLines: List<CheckoutAmountLine> = emptyList(),
    val discountLines: List<CheckoutAmountLine> = emptyList(),
    val shippingLines: List<CheckoutAmountLine> = emptyList(),
)

@ObjCName("CheckoutLineItem")
data class CheckoutLineItem(
    val name: String,
    val quantity: Long,
    val unitPrice: Long,
)

@ObjCName("CheckoutAmountLine")
data class CheckoutAmountLine(
    val description: String,
    val amount: Long,
)

@ObjCName("CheckoutProvider")
data class CheckoutProvider(
    val id: String,
    val name: String,
    val paymentMethod: String,
)

@ObjCName("CheckoutError")
sealed class CheckoutError {
    @ObjCName("CheckoutValidationError")
    data class ValidationError(
        val message: String,
    ) : CheckoutError()

    @ObjCName("CheckoutNetworkError")
    data class NetworkError(
        val message: String,
    ) : CheckoutError()

    @ObjCName("CheckoutApiError")
    data class ApiError(
        val code: String,
        val message: String,
    ) : CheckoutError()
}
