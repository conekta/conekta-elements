package io.conekta.elements.dtos

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
data class ResolveAppleCompanyIdInput(
  val isIntegration: Boolean,
  val companyId: String,
)
