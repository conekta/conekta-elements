package io.conekta.elements.tokenizer.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Card data sent inside the encrypted payload — mirrors int-payment-component's IFormCardData.
 */
@Serializable
data class CardDataDto(
    val cvc: String,
    @SerialName("exp_month") val expMonth: String,
    @SerialName("exp_year") val expYear: String,
    val name: String,
    val number: String,
)

/**
 * Wrapper that contains the card object — mirrors int-payment-component's TokenDTO.
 */
@Serializable
data class CardPayloadDto(
    val card: CardDataDto,
)

/**
 * Successful token response from the API.
 */
@Serializable
data class TokenResponseDto(
    val id: String,
    val livemode: Boolean = false,
    val used: Boolean = false,
    @SerialName("object") val objectType: String = "",
)

/**
 * Error response from the API.
 */
@Serializable
data class TokenErrorResponseDto(
    @SerialName("object") val objectType: String = "error",
    val type: String = "",
    val message: String = "",
    @SerialName("message_to_purchaser") val messageToPurchaser: String = "",
    val details: List<TokenErrorDetailDto> = emptyList(),
)

@Serializable
data class TokenErrorDetailDto(
    val message: String = "",
    val param: String? = null,
    val code: String = "",
)
