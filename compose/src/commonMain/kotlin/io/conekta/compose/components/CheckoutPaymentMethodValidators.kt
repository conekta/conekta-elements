package io.conekta.compose.components

import io.conekta.compose.checkout.CardFieldsState
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.tokenizer.validators.ValidationMessages
import io.conekta.elements.tokenizer.validators.validateForm

internal data class CheckoutPaymentMethodValidationInput(
    val cardFields: CardFieldsState,
    val cardValidationMessages: ValidationMessages,
)

internal interface CheckoutPaymentMethodValidator {
    fun canSubmit(input: CheckoutPaymentMethodValidationInput): Boolean

    fun validateBeforeSubmit(input: CheckoutPaymentMethodValidationInput): Boolean
}

internal object CheckoutPaymentMethodValidators {
    fun forMethod(methodKey: String): CheckoutPaymentMethodValidator =
        when (methodKey) {
            CheckoutPaymentMethods.CARD -> CardCheckoutPaymentMethodValidator
            CheckoutPaymentMethods.CASH -> PassThroughPaymentMethodValidator
            CheckoutPaymentMethods.BANK_TRANSFER -> PassThroughPaymentMethodValidator
            else -> PassThroughPaymentMethodValidator
        }
}

private object PassThroughPaymentMethodValidator : CheckoutPaymentMethodValidator {
    override fun canSubmit(input: CheckoutPaymentMethodValidationInput): Boolean = true

    override fun validateBeforeSubmit(input: CheckoutPaymentMethodValidationInput): Boolean = true
}

private object CardCheckoutPaymentMethodValidator : CheckoutPaymentMethodValidator {
    override fun canSubmit(input: CheckoutPaymentMethodValidationInput): Boolean = !validateCard(input).hasError

    override fun validateBeforeSubmit(input: CheckoutPaymentMethodValidationInput): Boolean {
        val result = validateCard(input)
        val fields = input.cardFields

        fields.cardholderNameError = result.cardholderName.isError
        fields.cardholderNameErrorMsg = result.cardholderName.message
        fields.cardNumberError = result.cardNumber.isError
        fields.cardNumberErrorMsg = result.cardNumber.message
        fields.expiryDateError = result.expiryDate.isError
        fields.expiryDateErrorMsg = result.expiryDate.message
        fields.cvvError = result.cvv.isError
        fields.cvvErrorMsg = result.cvv.message

        return !result.hasError
    }

    private fun validateCard(input: CheckoutPaymentMethodValidationInput) =
        validateForm(
            cardholderName = input.cardFields.cardholderName.text,
            cardNumber = input.cardFields.cardNumber.text,
            expiryDate = input.cardFields.expiryDate.text,
            cvv = input.cardFields.cvv.text,
            collectCardholderName = true,
            messages = input.cardValidationMessages,
        )
}
