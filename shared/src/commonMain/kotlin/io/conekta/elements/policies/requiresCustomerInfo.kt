package io.conekta.elements.domain.customer

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import io.conekta.elements.models.CustomerInfo

private fun isCustomerInfoComplete(customerInfo: CustomerInfo?): Boolean {
  return customerInfo?.name != null && customerInfo.email != null
}

@OptIn(ExperimentalJsExport::class)
@JsExport
fun requiresCustomerInfo(customerInfo: CustomerInfo?): Boolean {
  return !isCustomerInfoComplete(customerInfo)
}
