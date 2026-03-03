package io.conekta.elements.tokenizer.models

data class CardTokenizationData(
    val cardNumber: String,
    val expMonth: String,
    val expYear: String,
    val cvv: String,
    val cardholderName: String,
)
