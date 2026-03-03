package io.conekta.elements.checkout.models

object CheckoutPaymentMethods {
    const val CARD = "card"
    const val CASH = "cash"
    const val BANK_TRANSFER = "bank_transfer"

    fun toApiValue(method: String): String? =
        when (method) {
            CASH -> "Cash"
            BANK_TRANSFER -> "BankTransfer"
            CARD -> "Card"
            else -> null
        }
}
