package io.conekta.elements.dtos

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
data class MerchantEligibilityInput(
  val isHostedWithShopify: Boolean,
  val isIntegration: Boolean,
  val applePayForIntegrationEnabled: Boolean,
)
