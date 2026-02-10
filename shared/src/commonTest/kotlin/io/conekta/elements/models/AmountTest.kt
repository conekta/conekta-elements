package io.conekta.elements.models

import kotlin.test.Test
import kotlin.test.assertEquals

class AmountTest {
    // apiFormat() tests - Conversion from cents to decimal

    @Test
    fun `apiFormat converts cents to decimal format`() {
        val amount = Amount(12599L)
        assertEquals(125.99, amount.apiFormat())
    }

    @Test
    fun `apiFormat converts zero cents to zero decimal`() {
        val amount = Amount(0L)
        assertEquals(0.0, amount.apiFormat())
    }

    @Test
    fun `apiFormat converts whole dollar amount`() {
        val amount = Amount(10000L)
        assertEquals(100.0, amount.apiFormat())
    }

    // toFixed() tests - Formatting with different decimal places

    @Test
    fun `toFixed with 2 decimals formats amount correctly`() {
        val amount = Amount(12599L)
        assertEquals("125.99", amount.toFixed(2))
    }

    @Test
    fun `toFixed with 1 decimal rounds up when needed`() {
        val amount = Amount(12599L) // 125.99 rounds to 126.0
        assertEquals("126.0", amount.toFixed(1))
    }

    @Test
    fun `toFixed with 1 decimal rounds down when needed`() {
        val amount = Amount(12544L) // 125.44 rounds to 125.4
        assertEquals("125.4", amount.toFixed(1))
    }

    @Test
    fun `toFixed with 0 decimals rounds to nearest integer rounding up`() {
        val amount = Amount(12599L) // 125.99 rounds to 126
        assertEquals("126", amount.toFixed(0))
    }

    @Test
    fun `toFixed with 0 decimals rounds to nearest integer rounding down`() {
        val amount = Amount(12544L) // 125.44 rounds to 125
        assertEquals("125", amount.toFixed(0))
    }

    @Test
    fun `toFixed formats zero amount correctly`() {
        val amount = Amount(0L)
        assertEquals("0.00", amount.toFixed(2))
    }

    @Test
    fun `toFixed formats whole dollar amount with decimals`() {
        val amount = Amount(10000L)
        assertEquals("100.00", amount.toFixed(2))
    }

    // Edge cases

    @Test
    fun `apiFormat handles single cent`() {
        val amount = Amount(1L)
        assertEquals(0.01, amount.apiFormat())
    }

    @Test
    fun `apiFormat handles large amount`() {
        val amount = Amount(9999999L)
        assertEquals(99999.99, amount.apiFormat())
    }

    @Test
    fun `toFixed with 3 decimals adds trailing zero`() {
        val amount = Amount(12500L)
        assertEquals("125.000", amount.toFixed(3))
    }

    @Test
    fun `toString returns raw value as string`() {
        val amount = Amount(26070L)
        assertEquals("26070", amount.toString())
    }

    @Test
    fun `toString for zero returns zero string`() {
        val amount = Amount(0L)
        assertEquals("0", amount.toString())
    }

    @Test
    fun `apiFormat for 99 cents`() {
        val amount = Amount(99L)
        assertEquals(0.99, amount.apiFormat())
    }

    @Test
    fun `toFixed with 2 decimals for single cent`() {
        val amount = Amount(1L)
        assertEquals("0.01", amount.toFixed(2))
    }
}
