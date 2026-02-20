package io.conekta.elements.models

import kotlinx.serialization.Serializable

@Serializable
data class FeatureFlag(
  val id: String,
  val key: String,
  val value: Boolean? = null,
)
