package io.conekta.elements.domain.customer

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import io.conekta.elements.dtos.ResolveAppleCompanyIdInput

@OptIn(ExperimentalJsExport::class)
@JsExport
fun resolveAppleCompanyId(input: ResolveAppleCompanyIdInput): String =
  if (input.isIntegration) input.companyId else "merchant.io.conekta"