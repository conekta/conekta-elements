package io.conekta.elements.dtos

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@JsExport
@Serializable
data class PlanDto(
  val id: String,
  val name: String,
  val amount: Int,
  val currency: String,

  val interval: String,
  val frequency: Int,
  val expiryCount: Int,

  val subscriptionStart: Int,
  val subscriptionEnd: Int,

  val trialStart: Int,
  val trialEnd: Int,
  val trialPeriodDays: Int,

  val liveMode: Boolean,
  val createdAt: Int,
)
