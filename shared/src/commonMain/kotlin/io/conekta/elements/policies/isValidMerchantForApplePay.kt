package io.conekta.elements.policies

import io.conekta.elements.dtos.MerchantEligibilityInput
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
fun isValidMerchantForApplePay(input: MerchantEligibilityInput): Boolean {
  val isIntegrationWithoutApplePay =
    input.isIntegration && !input.applePayForIntegrationEnabled

  return !(input.isHostedWithShopify || isIntegrationWithoutApplePay)
}
