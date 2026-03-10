package io.conekta.elements.dtos

import io.conekta.elements.models.CheckoutStatus
import io.conekta.elements.orchestrator.PaymentMethodType
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
data class ApplePayEligibilityInput(
  val allowedPaymentMethods: Array<PaymentMethodType>,
  val checkoutStatus: CheckoutStatus,
  val withSubscription: Boolean,
  val paymentMethod: PaymentMethodType,
  val isValidMerchantForApplePay: Boolean,
)
