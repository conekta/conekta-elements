package io.conekta.elements.policies

import io.conekta.elements.models.CheckoutStatus
import io.conekta.elements.dtos.ApplePayEligibilityInput
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
fun isApplePayEligibleForCheckout(input: ApplePayEligibilityInput): Boolean {
  val isAllowedForCheckout = input.allowedPaymentMethods.any { it == input.paymentMethod }
  val isValidCheckoutStatus = input.checkoutStatus == CheckoutStatus.ISSUED

  return isAllowedForCheckout &&
    isValidCheckoutStatus &&
    !input.withSubscription &&
    input.isValidMerchantForApplePay
}
