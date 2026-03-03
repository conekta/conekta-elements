package io.conekta.compose.components

import androidx.compose.ui.text.input.TextFieldValue
import io.conekta.compose.components.card.CardFieldsState
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.tokenizer.validators.ValidationMessages
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CheckoutPaymentMethodValidatorsTest {
    private val validationMessages =
        ValidationMessages(
            required = "Required",
            cardMinLength = "Card too short",
            invalidCard = "Invalid card",
            expiryYearInvalid = "Invalid date",
            cvvMinLength = "Invalid CVV",
        )

    @Test
    fun forMethodReturnsPassThroughValidatorForCashBankTransferAndUnknown() {
        val input = validationInput(cardNumber = "4111 1111 1111 1111")

        val methods =
            listOf(
                CheckoutPaymentMethods.CASH,
                CheckoutPaymentMethods.BANK_TRANSFER,
                "some_unknown_method",
            )

        methods.forEach { method ->
            val validator = CheckoutPaymentMethodValidators.forMethod(method)
            assertTrue(validator.canSubmit(input))
            assertTrue(validator.validateBeforeSubmit(input))
        }
    }

    @Test
    fun cardValidatorReturnsTrueForValidInput() {
        val input =
            validationInput(
                cardholderName = "John Doe",
                cardNumber = "4242 4242 4242 4242",
                expiryDate = "12/99",
                cvv = "123",
            )

        val validator = CheckoutPaymentMethodValidators.forMethod(CheckoutPaymentMethods.CARD)

        assertTrue(validator.canSubmit(input))
        assertTrue(validator.validateBeforeSubmit(input))
        assertFalse(input.cardFields.cardholderNameError)
        assertFalse(input.cardFields.cardNumberError)
        assertFalse(input.cardFields.expiryDateError)
        assertFalse(input.cardFields.cvvError)
        assertEquals(null, input.cardFields.cardholderNameErrorMsg)
        assertEquals(null, input.cardFields.cardNumberErrorMsg)
        assertEquals(null, input.cardFields.expiryDateErrorMsg)
        assertEquals(null, input.cardFields.cvvErrorMsg)
    }

    @Test
    fun cardValidatorSetsFieldErrorsForInvalidInput() {
        val input =
            validationInput(
                cardholderName = "",
                cardNumber = "",
                expiryDate = "",
                cvv = "",
            )

        val validator = CheckoutPaymentMethodValidators.forMethod(CheckoutPaymentMethods.CARD)

        assertFalse(validator.canSubmit(input))
        assertFalse(validator.validateBeforeSubmit(input))
        assertTrue(input.cardFields.cardholderNameError)
        assertTrue(input.cardFields.cardNumberError)
        assertTrue(input.cardFields.expiryDateError)
        assertTrue(input.cardFields.cvvError)
        assertEquals(validationMessages.required, input.cardFields.cardholderNameErrorMsg)
        assertEquals(validationMessages.required, input.cardFields.cardNumberErrorMsg)
        assertEquals(validationMessages.required, input.cardFields.expiryDateErrorMsg)
        assertEquals(validationMessages.required, input.cardFields.cvvErrorMsg)
    }

    private fun validationInput(
        cardholderName: String = "",
        cardNumber: String = "",
        expiryDate: String = "",
        cvv: String = "",
    ): CheckoutPaymentMethodValidationInput {
        val fields =
            CardFieldsState().apply {
                this.cardholderName = TextFieldValue(cardholderName)
                this.cardNumber = TextFieldValue(cardNumber)
                this.expiryDate = TextFieldValue(expiryDate)
                this.cvv = TextFieldValue(cvv)
            }
        return CheckoutPaymentMethodValidationInput(
            cardFields = fields,
            cardValidationMessages = validationMessages,
        )
    }
}
