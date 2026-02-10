package io.conekta.elements.tokenizer.validators

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CardValidatorsTest {
    // ValidationErrorMessages tests

    @Test
    fun `ValidationErrorMessages stores all error messages`() {
        val messages =
            ValidationErrorMessages(
                required = "Required",
                invalidCard = "Invalid card",
                expiredCard = "Expired card",
                invalidCvv = "Invalid CVV",
                onlyDigits = "Only digits",
            )
        assertEquals("Required", messages.required)
        assertEquals("Invalid card", messages.invalidCard)
        assertEquals("Expired card", messages.expiredCard)
        assertEquals("Invalid CVV", messages.invalidCvv)
        assertEquals("Only digits", messages.onlyDigits)
    }

    // isValidCardNumber tests - Luhn algorithm

    @Test
    fun `isValidCardNumber returns true for valid Visa card`() {
        assertTrue(isValidCardNumber("4242424242424242"))
    }

    @Test
    fun `isValidCardNumber returns true for valid Mastercard`() {
        assertTrue(isValidCardNumber("5555555555554444"))
    }

    @Test
    fun `isValidCardNumber returns true for valid Amex`() {
        assertTrue(isValidCardNumber("378282246310005"))
    }

    @Test
    fun `isValidCardNumber returns false for invalid Luhn number`() {
        assertFalse(isValidCardNumber("4242424242424241"))
    }

    @Test
    fun `isValidCardNumber returns false for too short number`() {
        assertFalse(isValidCardNumber("424242"))
    }

    @Test
    fun `isValidCardNumber returns false for empty string`() {
        assertFalse(isValidCardNumber(""))
    }

    @Test
    fun `isValidCardNumber returns false for number with 12 digits`() {
        assertFalse(isValidCardNumber("424242424242"))
    }

    @Test
    fun `isValidCardNumber accepts 13 digit number with valid Luhn`() {
        assertTrue(isValidCardNumber("4222222222222"))
    }

    @Test
    fun `isValidCardNumber returns false for number exceeding 19 digits`() {
        assertFalse(isValidCardNumber("42424242424242424242"))
    }

    @Test
    fun `isValidCardNumber filters non-digit characters`() {
        assertTrue(isValidCardNumber("4242 4242 4242 4242"))
    }

    // isValidExpiryDate tests

    @Test
    fun `isValidExpiryDate returns true for valid month and year`() {
        assertTrue(isValidExpiryDate("12", "26"))
    }

    @Test
    fun `isValidExpiryDate returns true for month 01`() {
        assertTrue(isValidExpiryDate("01", "30"))
    }

    @Test
    fun `isValidExpiryDate returns false for month 00`() {
        assertFalse(isValidExpiryDate("00", "26"))
    }

    @Test
    fun `isValidExpiryDate returns false for month 13`() {
        assertFalse(isValidExpiryDate("13", "26"))
    }

    @Test
    fun `isValidExpiryDate returns false for non-numeric month`() {
        assertFalse(isValidExpiryDate("ab", "26"))
    }

    @Test
    fun `isValidExpiryDate returns false for non-numeric year`() {
        assertFalse(isValidExpiryDate("12", "ab"))
    }

    @Test
    fun `isValidExpiryDate returns false for 4 digit year`() {
        assertFalse(isValidExpiryDate("12", "2026"))
    }

    @Test
    fun `isValidExpiryDate returns false for 1 digit year`() {
        assertFalse(isValidExpiryDate("12", "6"))
    }

    // isValidCvv tests

    @Test
    fun `isValidCvv returns true for 3 digit cvv`() {
        assertTrue(isValidCvv("123"))
    }

    @Test
    fun `isValidCvv returns true for 4 digit cvv`() {
        assertTrue(isValidCvv("1234"))
    }

    @Test
    fun `isValidCvv returns false for 2 digit cvv`() {
        assertFalse(isValidCvv("12"))
    }

    @Test
    fun `isValidCvv returns false for 5 digit cvv`() {
        assertFalse(isValidCvv("12345"))
    }

    @Test
    fun `isValidCvv returns false for empty string`() {
        assertFalse(isValidCvv(""))
    }

    @Test
    fun `isValidCvv filters non-digit characters`() {
        assertTrue(isValidCvv("12a3"))
    }

    @Test
    fun `isValidCvv accepts brand parameter`() {
        assertTrue(isValidCvv("1234", "AMEX"))
    }
}
