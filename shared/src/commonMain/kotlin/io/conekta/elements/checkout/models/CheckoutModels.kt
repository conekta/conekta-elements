@file:OptIn(kotlin.experimental.ExperimentalObjCName::class)

package io.conekta.elements.checkout.models

import io.conekta.elements.localization.ConektaLanguage
import io.conekta.elements.network.ConektaServers
import kotlin.native.ObjCName

@ObjCName("CheckoutConfig")
data class CheckoutConfig(
    val checkoutRequestId: String,
    val publicKey: String,
    val jwtToken: String,
    val merchantName: String = "Demo Store",
    val baseUrl: String = ConektaServers.CHECKOUT_PRODUCTION_BASE_URL,
    val languageTag: String = ConektaLanguage.ES,
    val tokenizerBaseUrl: String = ConektaServers.TOKENIZER_PRODUCTION_BASE_URL,
    val tokenizerRsaPublicKey: String? = null,
) {
    constructor(
        checkoutRequestId: String,
        publicKey: String,
        jwtToken: String,
        merchantName: String,
        baseUrl: String,
        tokenizerBaseUrl: String,
    ) : this(
        checkoutRequestId = checkoutRequestId,
        publicKey = publicKey,
        jwtToken = jwtToken,
        merchantName = merchantName,
        baseUrl = baseUrl,
        languageTag = ConektaLanguage.ES,
        tokenizerBaseUrl = tokenizerBaseUrl,
        tokenizerRsaPublicKey = null,
    )

    constructor(
        checkoutRequestId: String,
        publicKey: String,
        jwtToken: String,
        merchantName: String,
        baseUrl: String,
        tokenizerBaseUrl: String,
        tokenizerRsaPublicKey: String,
    ) : this(
        checkoutRequestId = checkoutRequestId,
        publicKey = publicKey,
        jwtToken = jwtToken,
        merchantName = merchantName,
        baseUrl = baseUrl,
        languageTag = ConektaLanguage.ES,
        tokenizerBaseUrl = tokenizerBaseUrl,
        tokenizerRsaPublicKey = tokenizerRsaPublicKey,
    )

    constructor(
        checkoutRequestId: String,
        publicKey: String,
        jwtToken: String,
        merchantName: String,
    ) : this(
        checkoutRequestId = checkoutRequestId,
        publicKey = publicKey,
        jwtToken = jwtToken,
        merchantName = merchantName,
        baseUrl = ConektaServers.CHECKOUT_PRODUCTION_BASE_URL,
        languageTag = ConektaLanguage.ES,
        tokenizerBaseUrl = ConektaServers.TOKENIZER_PRODUCTION_BASE_URL,
        tokenizerRsaPublicKey = null,
    )

    constructor(
        checkoutRequestId: String,
        publicKey: String,
        jwtToken: String,
        merchantName: String,
        baseUrl: String,
    ) : this(
        checkoutRequestId = checkoutRequestId,
        publicKey = publicKey,
        jwtToken = jwtToken,
        merchantName = merchantName,
        baseUrl = baseUrl,
        languageTag = ConektaLanguage.ES,
        tokenizerBaseUrl = ConektaServers.TOKENIZER_PRODUCTION_BASE_URL,
        tokenizerRsaPublicKey = null,
    )

    constructor(
        checkoutRequestId: String,
        publicKey: String,
        jwtToken: String,
    ) : this(
        checkoutRequestId = checkoutRequestId,
        publicKey = publicKey,
        jwtToken = jwtToken,
        merchantName = "Demo Store",
        baseUrl = ConektaServers.CHECKOUT_PRODUCTION_BASE_URL,
        languageTag = ConektaLanguage.ES,
        tokenizerBaseUrl = ConektaServers.TOKENIZER_PRODUCTION_BASE_URL,
        tokenizerRsaPublicKey = null,
    )
}

@ObjCName("CheckoutResult")
data class CheckoutResult(
    val orderId: String,
    val checkoutId: String,
    val name: String = "",
    val amount: Long,
    val currency: String,
    val allowedPaymentMethods: List<String>,
    val providers: List<CheckoutProvider> = emptyList(),
    val lineItems: List<CheckoutLineItem> = emptyList(),
    val taxLines: List<CheckoutAmountLine> = emptyList(),
    val discountLines: List<CheckoutAmountLine> = emptyList(),
    val shippingLines: List<CheckoutAmountLine> = emptyList(),
    val email: String = "",
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
    val productType: String = "",
)

@ObjCName("CheckoutOrderResult")
data class CheckoutOrderResult(
    val orderId: String,
    val status: String = "",
    val charges: List<CheckoutCharge> = emptyList(),
)

@ObjCName("CheckoutCharge")
data class CheckoutCharge(
    val amount: Long = 0,
    val currency: String = CurrencyCodes.MXN,
    val status: String = "",
    val paymentMethod: CheckoutChargePaymentMethod? = null,
)

@ObjCName("CheckoutChargePaymentMethod")
data class CheckoutChargePaymentMethod(
    val type: String = "",
    val reference: String = "",
    val clabe: String = "",
    val barcodeUrl: String = "",
    val expiresAt: Long = 0,
    val serviceName: String = "",
    val storeName: String = "",
    val provider: String = "",
    val agreement: String = "",
    val name: String = "",
    val productType: String = "",
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
