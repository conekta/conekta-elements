package io.conekta.elements.checkout.models

import io.conekta.elements.tokenizer.api.TokenizerApiService
import io.conekta.elements.tokenizer.formatters.CardInputFormatters

sealed interface CheckoutPaymentMethod {
    val methodKey: String

    suspend fun resolveTokenId(): String?

    class Cash : CheckoutPaymentMethod {
        override val methodKey = CheckoutPaymentMethods.CASH

        override suspend fun resolveTokenId(): String? = null
    }

    class BankTransfer : CheckoutPaymentMethod {
        override val methodKey = CheckoutPaymentMethods.BANK_TRANSFER

        override suspend fun resolveTokenId(): String? = null
    }

    class Card(
        private val tokenizerService: TokenizerApiService,
        private val cardNumber: String,
        private val expiryDate: String,
        private val cvv: String,
        private val cardholderName: String,
    ) : CheckoutPaymentMethod {
        override val methodKey = CheckoutPaymentMethods.CARD

        override suspend fun resolveTokenId(): String? {
            val cardData =
                CardInputFormatters.extractTokenizationData(
                    cardNumber = cardNumber,
                    expiryDate = expiryDate,
                    cvv = cvv,
                    cardholderName = cardholderName,
                )
            return tokenizerService
                .tokenize(
                    cardNumber = cardData.cardNumber,
                    expMonth = cardData.expMonth,
                    expYear = cardData.expYear,
                    cvc = cardData.cvv,
                    cardholderName = cardData.cardholderName,
                ).getOrThrow()
                .token
        }
    }

    companion object {
        fun from(
            methodKey: String,
            tokenizerService: TokenizerApiService,
            cardNumber: String,
            expiryDate: String,
            cvv: String,
            cardholderName: String,
        ): CheckoutPaymentMethod =
            when (methodKey) {
                CheckoutPaymentMethods.CASH -> Cash()
                CheckoutPaymentMethods.BANK_TRANSFER -> BankTransfer()
                CheckoutPaymentMethods.CARD ->
                    Card(
                        tokenizerService = tokenizerService,
                        cardNumber = cardNumber,
                        expiryDate = expiryDate,
                        cvv = cvv,
                        cardholderName = cardholderName,
                    )
                else -> Cash()
            }
    }
}
