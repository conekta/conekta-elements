package io.conekta.compose.tokenizer

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.conekta.elements.tokenizer.models.CardBrand
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CardFormattersTest {
    private fun textFieldValue(text: String) = TextFieldValue(text, TextRange(text.length))

    // formatCardNumber tests

    @Test
    fun `formatCardNumber formats 16 digits with spaces every 4`() {
        val result = CardFormatters.formatCardNumber(textFieldValue("4242424242424242"))
        assertEquals("4242 4242 4242 4242", result.text)
    }

    @Test
    fun `formatCardNumber strips non-digit characters`() {
        val result = CardFormatters.formatCardNumber(textFieldValue("4242-4242-4242-4242"))
        assertEquals("4242 4242 4242 4242", result.text)
    }

    @Test
    fun `formatCardNumber limits to 16 digits`() {
        val result = CardFormatters.formatCardNumber(textFieldValue("42424242424242429999"))
        assertEquals("4242 4242 4242 4242", result.text)
    }

    @Test
    fun `formatCardNumber handles partial input`() {
        val result = CardFormatters.formatCardNumber(textFieldValue("424"))
        assertEquals("424", result.text)
    }

    @Test
    fun `formatCardNumber handles empty input`() {
        val result = CardFormatters.formatCardNumber(textFieldValue(""))
        assertEquals("", result.text)
    }

    @Test
    fun `formatCardNumber handles 8 digits`() {
        val result = CardFormatters.formatCardNumber(textFieldValue("42424242"))
        assertEquals("4242 4242", result.text)
    }

    @Test
    fun `formatCardNumber adds space after first 4 digits`() {
        val result = CardFormatters.formatCardNumber(textFieldValue("42425"))
        assertEquals("4242 5", result.text)
    }

    // formatExpiryDate tests

    @Test
    fun `formatExpiryDate formats valid date as MM slash YY`() {
        val result = CardFormatters.formatExpiryDate(textFieldValue("1226"))
        assertEquals("12/26", result.text)
    }

    @Test
    fun `formatExpiryDate handles empty input`() {
        val result = CardFormatters.formatExpiryDate(textFieldValue(""))
        assertEquals("", result.text)
    }

    @Test
    fun `formatExpiryDate auto-prefixes digit greater than 1 with zero`() {
        val result = CardFormatters.formatExpiryDate(textFieldValue("3"))
        assertEquals("03", result.text)
    }

    @Test
    fun `formatExpiryDate keeps single digit 1 without prefix`() {
        val result = CardFormatters.formatExpiryDate(textFieldValue("1"))
        assertEquals("1", result.text)
    }

    @Test
    fun `formatExpiryDate keeps single digit 0 without prefix`() {
        val result = CardFormatters.formatExpiryDate(textFieldValue("0"))
        assertEquals("0", result.text)
    }

    @Test
    fun `formatExpiryDate rejects month greater than 12`() {
        val result = CardFormatters.formatExpiryDate(textFieldValue("13"))
        assertEquals("1", result.text)
    }

    @Test
    fun `formatExpiryDate allows month 12`() {
        val result = CardFormatters.formatExpiryDate(textFieldValue("12"))
        assertEquals("12", result.text)
    }

    @Test
    fun `formatExpiryDate allows month 01`() {
        val result = CardFormatters.formatExpiryDate(textFieldValue("01"))
        assertEquals("01", result.text)
    }

    @Test
    fun `formatExpiryDate rejects year starting with 0`() {
        val result = CardFormatters.formatExpiryDate(textFieldValue("120"))
        assertEquals("12", result.text)
    }

    @Test
    fun `formatExpiryDate rejects year starting with 1`() {
        val result = CardFormatters.formatExpiryDate(textFieldValue("121"))
        assertEquals("12", result.text)
    }

    @Test
    fun `formatExpiryDate rejects year less than 26`() {
        val result = CardFormatters.formatExpiryDate(textFieldValue("1225"))
        assertEquals("12/2", result.text)
    }

    @Test
    fun `formatExpiryDate accepts year 26`() {
        val result = CardFormatters.formatExpiryDate(textFieldValue("1226"))
        assertEquals("12/26", result.text)
    }

    @Test
    fun `formatExpiryDate accepts year 30`() {
        val result = CardFormatters.formatExpiryDate(textFieldValue("0630"))
        assertEquals("06/30", result.text)
    }

    @Test
    fun `formatExpiryDate strips non-digit characters`() {
        val result = CardFormatters.formatExpiryDate(textFieldValue("12/26"))
        assertEquals("12/26", result.text)
    }

    @Test
    fun `formatExpiryDate limits to 4 digits`() {
        val result = CardFormatters.formatExpiryDate(textFieldValue("122699"))
        assertEquals("12/26", result.text)
    }

    @Test
    fun `formatExpiryDate shows partial year`() {
        val result = CardFormatters.formatExpiryDate(textFieldValue("123"))
        assertEquals("12/3", result.text)
    }

    // formatCvv tests

    @Test
    fun `formatCvv accepts 3 digit cvv`() {
        val result = CardFormatters.formatCvv(textFieldValue("123"), CardBrand.VISA)
        assertEquals("123", result.text)
    }

    @Test
    fun `formatCvv accepts 4 digit cvv`() {
        val result = CardFormatters.formatCvv(textFieldValue("1234"), CardBrand.AMEX)
        assertEquals("1234", result.text)
    }

    @Test
    fun `formatCvv limits to 4 digits`() {
        val result = CardFormatters.formatCvv(textFieldValue("12345"), CardBrand.VISA)
        assertEquals("1234", result.text)
    }

    @Test
    fun `formatCvv strips non-digit characters`() {
        val result = CardFormatters.formatCvv(textFieldValue("12a3"), CardBrand.VISA)
        assertEquals("123", result.text)
    }

    @Test
    fun `formatCvv handles empty input`() {
        val result = CardFormatters.formatCvv(textFieldValue(""), CardBrand.VISA)
        assertEquals("", result.text)
    }

    // detectCardBrand tests

    @Test
    fun `detectCardBrand returns VISA for numbers starting with 4`() {
        assertEquals(CardBrand.VISA, CardFormatters.detectCardBrand("4242424242424242"))
    }

    @Test
    fun `detectCardBrand returns MASTERCARD for numbers starting with 5`() {
        assertEquals(CardBrand.MASTERCARD, CardFormatters.detectCardBrand("5555555555554444"))
    }

    @Test
    fun `detectCardBrand returns MASTERCARD for numbers starting with 2`() {
        assertEquals(CardBrand.MASTERCARD, CardFormatters.detectCardBrand("2221000000000009"))
    }

    @Test
    fun `detectCardBrand returns AMEX for numbers starting with 34`() {
        assertEquals(CardBrand.AMEX, CardFormatters.detectCardBrand("340000000000009"))
    }

    @Test
    fun `detectCardBrand returns AMEX for numbers starting with 37`() {
        assertEquals(CardBrand.AMEX, CardFormatters.detectCardBrand("370000000000002"))
    }

    @Test
    fun `detectCardBrand returns UNKNOWN for empty input`() {
        assertEquals(CardBrand.UNKNOWN, CardFormatters.detectCardBrand(""))
    }

    @Test
    fun `detectCardBrand returns UNKNOWN for unrecognized prefix`() {
        assertEquals(CardBrand.UNKNOWN, CardFormatters.detectCardBrand("9999999999999999"))
    }

    @Test
    fun `detectCardBrand strips non-digit characters`() {
        assertEquals(CardBrand.VISA, CardFormatters.detectCardBrand("4242 4242 4242 4242"))
    }

    // isValidCardNumber tests

    @Test
    fun `isValidCardNumber returns true for valid Visa card`() {
        assertTrue(CardFormatters.isValidCardNumber("4242424242424242"))
    }

    @Test
    fun `isValidCardNumber returns false for invalid number`() {
        assertFalse(CardFormatters.isValidCardNumber("1234567890123456"))
    }

    @Test
    fun `isValidCardNumber returns false for too short number`() {
        assertFalse(CardFormatters.isValidCardNumber("424242"))
    }

    @Test
    fun `isValidCardNumber returns false for empty string`() {
        assertFalse(CardFormatters.isValidCardNumber(""))
    }

    // isValidExpiryDate tests

    @Test
    fun `isValidExpiryDate returns true for valid date`() {
        assertTrue(CardFormatters.isValidExpiryDate("12/26"))
    }

    @Test
    fun `isValidExpiryDate returns false for incomplete date`() {
        assertFalse(CardFormatters.isValidExpiryDate("12"))
    }

    @Test
    fun `isValidExpiryDate returns false for invalid month`() {
        assertFalse(CardFormatters.isValidExpiryDate("13/26"))
    }

    @Test
    fun `isValidExpiryDate returns false for month zero`() {
        assertFalse(CardFormatters.isValidExpiryDate("00/26"))
    }

    @Test
    fun `isValidExpiryDate returns false for empty string`() {
        assertFalse(CardFormatters.isValidExpiryDate(""))
    }

    // isValidCvv tests

    @Test
    fun `isValidCvv returns true for 3 digit cvv`() {
        assertTrue(CardFormatters.isValidCvv("123", CardBrand.VISA))
    }

    @Test
    fun `isValidCvv returns true for 4 digit cvv`() {
        assertTrue(CardFormatters.isValidCvv("1234", CardBrand.AMEX))
    }

    @Test
    fun `isValidCvv returns false for 2 digit cvv`() {
        assertFalse(CardFormatters.isValidCvv("12", CardBrand.VISA))
    }

    @Test
    fun `isValidCvv returns false for empty cvv`() {
        assertFalse(CardFormatters.isValidCvv("", CardBrand.VISA))
    }
}
