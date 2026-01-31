package io.conekta.elements.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConektaApiError(
    val type: String = "",
    @SerialName("log_id")
    val logId: String = "",
    val details: List<ErrorDetail> = emptyList(),
)

@Serializable
data class ErrorDetail(
    @SerialName("debug_message")
    val debugMessage: String = "",
    val message: String = "",
    val param: String? = null,
    val code: String = "",
)
