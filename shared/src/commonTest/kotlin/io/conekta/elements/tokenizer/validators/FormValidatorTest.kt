package io.conekta.elements.tokenizer.validators

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FormValidatorTest {
    private val messages =
        ValidationMessages(
            required = "Required",
            cardMinLength = "Card too short",
            expiryYearInvalid = "Invalid expiry",
            cvvMinLength = "CVV too short",
        )

    // FieldError

    @Test
    fun `FieldError default has no error`() {
        val error = FieldError()
        assertFalse(error.isError)
        assertNull(error.message)
    }

    @Test
    fun `FieldError with error stores message`() {
        val error = FieldError(isError = true, message = "Required")
        assertTrue(error.isError)
        assertEquals("Required", error.message)
    }

    // ValidationResult

    @Test
    fun `ValidationResult hasError returns false when all fields ok`() {
        val result = ValidationResult()
        assertFalse(result.hasError)
    }

    @Test
    fun `ValidationResult hasError returns true when cardNumber has error`() {
        val result = ValidationResult(cardNumber = FieldError(isError = true, message = "err"))
        assertTrue(result.hasError)
    }

    @Test
    fun `ValidationResult hasError returns true when cvv has error`() {
        val result = ValidationResult(cvv = FieldError(isError = true, message = "err"))
        assertTrue(result.hasError)
    }

    @Test
    fun `ValidationResult hasError returns true when expiryDate has error`() {
        val result = ValidationResult(expiryDate = FieldError(isError = true, message = "err"))
        assertTrue(result.hasError)
    }

    @Test
    fun `ValidationResult hasError returns true when cardholderName has error`() {
        val result = ValidationResult(cardholderName = FieldError(isError = true, message = "err"))
        assertTrue(result.hasError)
    }

    // validateForm - valid inputs

    @Test
    fun `validateForm returns no errors for valid inputs`() {
        val result =
            validateForm(
                cardholderName = "John Doe",
                cardNumber = "4242 4242 4242 4242",
                expiryDate = "12/26",
                cvv = "123",
                collectCardholderName = true,
                messages = messages,
            )
        assertFalse(result.hasError)
    }

    @Test
    fun `validateForm skips cardholderName when not collected`() {
        val result =
            validateForm(
                cardholderName = "",
                cardNumber = "4242 4242 4242 4242",
                expiryDate = "12/26",
                cvv = "123",
                collectCardholderName = false,
                messages = messages,
            )
        assertFalse(result.cardholderName.isError)
    }

    // validateForm - empty fields

    @Test
    fun `validateForm detects empty cardholderName when collected`() {
        val result =
            validateForm(
                cardholderName = "",
                cardNumber = "4242 4242 4242 4242",
                expiryDate = "12/26",
                cvv = "123",
                collectCardholderName = true,
                messages = messages,
            )
        assertTrue(result.cardholderName.isError)
        assertEquals("Required", result.cardholderName.message)
    }

    @Test
    fun `validateForm detects empty cardNumber`() {
        val result =
            validateForm(
                cardholderName = "John",
                cardNumber = "",
                expiryDate = "12/26",
                cvv = "123",
                collectCardholderName = true,
                messages = messages,
            )
        assertTrue(result.cardNumber.isError)
        assertEquals("Required", result.cardNumber.message)
    }

    @Test
    fun `validateForm detects empty expiryDate`() {
        val result =
            validateForm(
                cardholderName = "John",
                cardNumber = "4242 4242 4242 4242",
                expiryDate = "",
                cvv = "123",
                collectCardholderName = true,
                messages = messages,
            )
        assertTrue(result.expiryDate.isError)
        assertEquals("Required", result.expiryDate.message)
    }

    @Test
    fun `validateForm detects empty cvv`() {
        val result =
            validateForm(
                cardholderName = "John",
                cardNumber = "4242 4242 4242 4242",
                expiryDate = "12/26",
                cvv = "",
                collectCardholderName = true,
                messages = messages,
            )
        assertTrue(result.cvv.isError)
        assertEquals("Required", result.cvv.message)
    }

    // validateForm - invalid values

    @Test
    fun `validateForm detects invalid card number`() {
        val result =
            validateForm(
                cardholderName = "John",
                cardNumber = "1234",
                expiryDate = "12/26",
                cvv = "123",
                collectCardholderName = true,
                messages = messages,
            )
        assertTrue(result.cardNumber.isError)
        assertEquals("Card too short", result.cardNumber.message)
    }

    @Test
    fun `validateForm detects invalid expiry date`() {
        val result =
            validateForm(
                cardholderName = "John",
                cardNumber = "4242 4242 4242 4242",
                expiryDate = "13/26",
                cvv = "123",
                collectCardholderName = true,
                messages = messages,
            )
        assertTrue(result.expiryDate.isError)
        assertEquals("Invalid expiry", result.expiryDate.message)
    }

    @Test
    fun `validateForm detects invalid cvv`() {
        val result =
            validateForm(
                cardholderName = "John",
                cardNumber = "4242 4242 4242 4242",
                expiryDate = "12/26",
                cvv = "12",
                collectCardholderName = true,
                messages = messages,
            )
        assertTrue(result.cvv.isError)
        assertEquals("CVV too short", result.cvv.message)
    }

    @Test
    fun `validateForm detects multiple errors at once`() {
        val result =
            validateForm(
                cardholderName = "",
                cardNumber = "",
                expiryDate = "",
                cvv = "",
                collectCardholderName = true,
                messages = messages,
            )
        assertTrue(result.hasError)
        assertTrue(result.cardholderName.isError)
        assertTrue(result.cardNumber.isError)
        assertTrue(result.expiryDate.isError)
        assertTrue(result.cvv.isError)
    }

    // validateForm - edge cases

    @Test
    fun `validateForm returns no error for cardholderName when not collected even with value`() {
        val result =
            validateForm(
                cardholderName = "John",
                cardNumber = "4242 4242 4242 4242",
                expiryDate = "12/26",
                cvv = "123",
                collectCardholderName = false,
                messages = messages,
            )
        assertFalse(result.cardholderName.isError)
    }

    @Test
    fun `validateForm detects partial expiry date`() {
        val result =
            validateForm(
                cardholderName = "John",
                cardNumber = "4242 4242 4242 4242",
                expiryDate = "12/",
                cvv = "123",
                collectCardholderName = true,
                messages = messages,
            )
        assertTrue(result.expiryDate.isError)
        assertEquals("Invalid expiry", result.expiryDate.message)
    }

    @Test
    fun `validateForm detects single digit expiry`() {
        val result =
            validateForm(
                cardholderName = "John",
                cardNumber = "4242 4242 4242 4242",
                expiryDate = "1",
                cvv = "123",
                collectCardholderName = true,
                messages = messages,
            )
        assertTrue(result.expiryDate.isError)
        assertEquals("Invalid expiry", result.expiryDate.message)
    }

    @Test
    fun `validateForm valid AMEX cvv with 4 digits`() {
        val result =
            validateForm(
                cardholderName = "John",
                cardNumber = "3782 8224 6310 005",
                expiryDate = "12/26",
                cvv = "1234",
                collectCardholderName = true,
                messages = messages,
            )
        assertFalse(result.cvv.isError)
    }

    @Test
    fun `validateForm whitespace-only cardholderName is error`() {
        val result =
            validateForm(
                cardholderName = "   ",
                cardNumber = "4242 4242 4242 4242",
                expiryDate = "12/26",
                cvv = "123",
                collectCardholderName = true,
                messages = messages,
            )
        assertTrue(result.cardholderName.isError)
        assertEquals("Required", result.cardholderName.message)
    }

    @Test
    fun `validateForm whitespace-only cardNumber is error`() {
        val result =
            validateForm(
                cardholderName = "John",
                cardNumber = "   ",
                expiryDate = "12/26",
                cvv = "123",
                collectCardholderName = true,
                messages = messages,
            )
        assertTrue(result.cardNumber.isError)
        assertEquals("Required", result.cardNumber.message)
    }

    // ValidationMessages

    @Test
    fun `ValidationMessages stores all messages`() {
        assertEquals("Required", messages.required)
        assertEquals("Card too short", messages.cardMinLength)
        assertEquals("Invalid expiry", messages.expiryYearInvalid)
        assertEquals("CVV too short", messages.cvvMinLength)
    }
}
