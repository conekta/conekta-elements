package io.conekta.elements.dtos

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@JsExport
@Serializable
data class FeatureFlagDto(
  val id: String,
  val key: String,
  val value: Boolean? = null,
)
