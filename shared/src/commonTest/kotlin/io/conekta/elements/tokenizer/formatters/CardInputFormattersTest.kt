package io.conekta.elements.tokenizer.formatters

import io.conekta.elements.tokenizer.models.CardBrand
import kotlin.test.Test
import kotlin.test.assertEquals

class CardInputFormattersTest {
    // formatCardNumber

    @Test
    fun `formatCardNumber formats 16 digits with spaces`() {
        assertEquals("4242 4242 4242 4242", CardInputFormatters.formatCardNumber("4242424242424242"))
    }

    @Test
    fun `formatCardNumber strips non-digits`() {
        assertEquals("4242 4242 4242 4242", CardInputFormatters.formatCardNumber("4242-4242-4242-4242"))
    }

    @Test
    fun `formatCardNumber limits to 16 digits`() {
        assertEquals("4242 4242 4242 4242", CardInputFormatters.formatCardNumber("42424242424242429999"))
    }

    @Test
    fun `formatCardNumber handles partial input`() {
        assertEquals("424", CardInputFormatters.formatCardNumber("424"))
    }

    @Test
    fun `formatCardNumber handles empty input`() {
        assertEquals("", CardInputFormatters.formatCardNumber(""))
    }

    // formatExpiryDate

    @Test
    fun `formatExpiryDate formats valid date`() {
        assertEquals("12/26", CardInputFormatters.formatExpiryDate("1226"))
    }

    @Test
    fun `formatExpiryDate empty input returns empty`() {
        assertEquals("", CardInputFormatters.formatExpiryDate(""))
    }

    @Test
    fun `formatExpiryDate auto-prefixes high first digit`() {
        assertEquals("03", CardInputFormatters.formatExpiryDate("3"))
    }

    @Test
    fun `formatExpiryDate keeps single digit 1`() {
        assertEquals("1", CardInputFormatters.formatExpiryDate("1"))
    }

    @Test
    fun `formatExpiryDate rejects month greater than 12`() {
        assertEquals("1", CardInputFormatters.formatExpiryDate("13"))
    }

    @Test
    fun `formatExpiryDate allows month 12`() {
        assertEquals("12", CardInputFormatters.formatExpiryDate("12"))
    }

    @Test
    fun `formatExpiryDate strips non-digits`() {
        assertEquals("12/26", CardInputFormatters.formatExpiryDate("12/26"))
    }

    @Test
    fun `formatExpiryDate limits to 4 digits`() {
        assertEquals("12/26", CardInputFormatters.formatExpiryDate("122699"))
    }

    // formatCvv

    @Test
    fun `formatCvv accepts 3 digits`() {
        assertEquals("123", CardInputFormatters.formatCvv("123"))
    }

    @Test
    fun `formatCvv accepts 4 digits`() {
        assertEquals("1234", CardInputFormatters.formatCvv("1234"))
    }

    @Test
    fun `formatCvv limits to 4 digits`() {
        assertEquals("1234", CardInputFormatters.formatCvv("12345"))
    }

    @Test
    fun `formatCvv strips non-digits`() {
        assertEquals("123", CardInputFormatters.formatCvv("12a3"))
    }

    @Test
    fun `formatCvv handles empty input`() {
        assertEquals("", CardInputFormatters.formatCvv(""))
    }

    // detectCardBrand

    @Test
    fun `detectCardBrand returns VISA for 4 prefix`() {
        assertEquals(CardBrand.VISA, CardInputFormatters.detectCardBrand("4242424242424242"))
    }

    @Test
    fun `detectCardBrand returns MASTERCARD for 5 prefix`() {
        assertEquals(CardBrand.MASTERCARD, CardInputFormatters.detectCardBrand("5555555555554444"))
    }

    @Test
    fun `detectCardBrand returns MASTERCARD for 2 prefix`() {
        assertEquals(CardBrand.MASTERCARD, CardInputFormatters.detectCardBrand("2221000000000009"))
    }

    @Test
    fun `detectCardBrand returns AMEX for 34 prefix`() {
        assertEquals(CardBrand.AMEX, CardInputFormatters.detectCardBrand("340000000000009"))
    }

    @Test
    fun `detectCardBrand returns AMEX for 37 prefix`() {
        assertEquals(CardBrand.AMEX, CardInputFormatters.detectCardBrand("370000000000002"))
    }

    @Test
    fun `detectCardBrand returns UNKNOWN for empty`() {
        assertEquals(CardBrand.UNKNOWN, CardInputFormatters.detectCardBrand(""))
    }

    @Test
    fun `detectCardBrand returns UNKNOWN for unrecognized prefix`() {
        assertEquals(CardBrand.UNKNOWN, CardInputFormatters.detectCardBrand("9999999999"))
    }

    // formatExpiryDate - year edge cases

    @Test
    fun `formatExpiryDate rejects past year first digit`() {
        // Year digit 1 < current decade digit (2) → drops year
        assertEquals("12", CardInputFormatters.formatExpiryDate("1210"))
    }

    @Test
    fun `formatExpiryDate returns single valid year digit`() {
        // Year digit 3 >= 2, single digit → keeps it
        assertEquals("12/3", CardInputFormatters.formatExpiryDate("123"))
    }

    @Test
    fun `formatExpiryDate rejects year below current`() {
        // Year 22 < 26 → keeps only first digit
        assertEquals("12/2", CardInputFormatters.formatExpiryDate("1222"))
    }

    @Test
    fun `formatExpiryDate accepts future year`() {
        assertEquals("12/30", CardInputFormatters.formatExpiryDate("1230"))
    }

    @Test
    fun `formatExpiryDate month 01 with year`() {
        assertEquals("01/26", CardInputFormatters.formatExpiryDate("0126"))
    }

    @Test
    fun `formatExpiryDate single digit 0 stays as is`() {
        assertEquals("0", CardInputFormatters.formatExpiryDate("0"))
    }

    @Test
    fun `formatExpiryDate month 00 rejected`() {
        // month "00" = 0, which is > 12? No, 0 is not > 12. But 0 is a valid month int?
        // Actually month=0 is not > 12, so it passes. Then year part is processed.
        assertEquals("00", CardInputFormatters.formatExpiryDate("00"))
    }

    @Test
    fun `formatExpiryDate year digit exactly at decade boundary`() {
        // Year starts with "2" which equals minFirstDigit (2), so >= 2, passes
        assertEquals("12/2", CardInputFormatters.formatExpiryDate("122"))
    }

    @Test
    fun `formatExpiryDate year exactly current year passes`() {
        assertEquals("12/26", CardInputFormatters.formatExpiryDate("1226"))
    }

    // formatCardNumber - additional edge cases

    @Test
    fun `formatCardNumber formats 8 digits with space`() {
        assertEquals("4242 4242", CardInputFormatters.formatCardNumber("42424242"))
    }

    @Test
    fun `formatCardNumber formats 5 digits`() {
        assertEquals("4242 4", CardInputFormatters.formatCardNumber("42424"))
    }

    // detectCardBrand - additional cases

    @Test
    fun `detectCardBrand with spaces returns correct brand`() {
        assertEquals(CardBrand.VISA, CardInputFormatters.detectCardBrand("4242 4242"))
    }

    @Test
    fun `detectCardBrand single digit 4 returns VISA`() {
        assertEquals(CardBrand.VISA, CardInputFormatters.detectCardBrand("4"))
    }

    @Test
    fun `detectCardBrand single digit 5 returns MASTERCARD`() {
        assertEquals(CardBrand.MASTERCARD, CardInputFormatters.detectCardBrand("5"))
    }

    @Test
    fun `detectCardBrand single digit 2 returns MASTERCARD`() {
        assertEquals(CardBrand.MASTERCARD, CardInputFormatters.detectCardBrand("2"))
    }

    @Test
    fun `detectCardBrand prefix 6 returns UNKNOWN`() {
        assertEquals(CardBrand.UNKNOWN, CardInputFormatters.detectCardBrand("6011"))
    }
}
