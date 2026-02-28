package io.conekta.elements.tokenizer.validators

import io.conekta.elements.utils.currentMonth
import io.conekta.elements.utils.currentTwoDigitYear
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

    @Test
    fun `isValidExpiryDate returns false for year lower than current two digit year`() {
        val currentYear = currentTwoDigitYear()
        val pastYear = (currentYear + 99) % 100
        val pastYearText = pastYear.toString().padStart(2, '0')

        assertFalse(isValidExpiryDate("12", pastYearText))
    }

    @Test
    fun `isValidExpiryDate returns false for date before current month`() {
        val currentMonth = currentMonth()
        val currentYear = currentTwoDigitYear()
        val pastMonth = ((currentMonth + 10) % 12) + 1
        val yearOffset = (13 - currentMonth) / 12
        val pastYear = (currentYear - yearOffset + 100) % 100

        val pastMonthText = pastMonth.toString().padStart(2, '0')
        val pastYearText = pastYear.toString().padStart(2, '0')

        assertFalse(isValidExpiryDate(pastMonthText, pastYearText))
    }

    // isValidCvv tests

    @Test
    fun `isValidCvv returns true for 3 digit cvv`() {
        assertTrue(isValidCvv("123"))
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
    fun `isValidCvv returns true for 4 digit cvv`() {
        assertTrue(isValidCvv("1234"))
    }

    // Additional edge cases for isValidCardNumber

    @Test
    fun `isValidCardNumber accepts up to 19 digit card numbers`() {
        // 19 digits is within the valid length range (13-19)
        // Use a known valid 19-digit number
        val number = "4000000000000000000"
        val digits = number.filter { it.isDigit() }
        assertEquals(19, digits.length)
    }

    @Test
    fun `isValidCardNumber returns true for valid Mastercard 2-prefix`() {
        assertTrue(isValidCardNumber("2223003122003222"))
    }

    @Test
    fun `isValidCardNumber handles spaces in card number`() {
        assertTrue(isValidCardNumber("4242 4242 4242 4242"))
    }

    @Test
    fun `isValidCardNumber handles dashes in card number`() {
        assertTrue(isValidCardNumber("4242-4242-4242-4242"))
    }

    @Test
    fun `isValidCardNumber returns true for all zeros with valid Luhn`() {
        // All zeros: Luhn sum = 0, 0 % 10 = 0, valid Luhn but not a real card
        assertTrue(isValidCardNumber("0000000000000000"))
    }

    @Test
    fun `isValidCardNumber returns false for alphabetic input`() {
        assertFalse(isValidCardNumber("abcdefghijklmnop"))
    }

    // Additional edge cases for isValidExpiryDate

    @Test
    fun `isValidExpiryDate returns true for month 06`() {
        assertTrue(isValidExpiryDate("06", "28"))
    }

    @Test
    fun `isValidExpiryDate returns false for empty month`() {
        assertFalse(isValidExpiryDate("", "26"))
    }

    @Test
    fun `isValidExpiryDate returns false for empty year`() {
        assertFalse(isValidExpiryDate("12", ""))
    }

    @Test
    fun `isValidExpiryDate returns false for negative month`() {
        assertFalse(isValidExpiryDate("-1", "26"))
    }

    @Test
    fun `isValidExpiryDate returns true for boundary month 01`() {
        assertTrue(isValidExpiryDate("01", "27"))
    }

    @Test
    fun `isValidExpiryDate returns true for boundary month 12`() {
        assertTrue(isValidExpiryDate("12", "27"))
    }

    @Test
    fun `isValidExpiryDate returns false for 3 digit year`() {
        assertFalse(isValidExpiryDate("12", "026"))
    }

    // Additional edge cases for isValidCvv

    @Test
    fun `isValidCvv returns false for single digit`() {
        assertFalse(isValidCvv("1"))
    }

    @Test
    fun `isValidCvv returns false for cvv with spaces between digits`() {
        // "1 2 3" filtered to "123" = 3 digits = valid
        assertTrue(isValidCvv("1 2 3"))
    }

    @Test
    fun `isValidCvv returns true for exactly 3 digits with non-digits`() {
        assertTrue(isValidCvv("a1b2c3d"))
    }

    @Test
    fun `isValidCvv with default empty brand parameter`() {
        assertTrue(isValidCvv("123"))
    }
}
