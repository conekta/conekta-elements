package io.conekta.compose.components

internal data class CardPaymentInput(
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String,
    val cardholderName: String,
)
