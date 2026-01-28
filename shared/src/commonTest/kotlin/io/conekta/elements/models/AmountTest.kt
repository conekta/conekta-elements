package io.conekta.elements.models

import kotlin.test.Test
import kotlin.test.assertEquals

class AmountTest {
    @Test
    fun `apiFormat divides by 100`() {
        val amount = Amount(12599L)
        assertEquals(125.99, amount.apiFormat())
    }

    @Test
    fun `toFixed formats with correct decimals`() {
        // Test with an amount that will round up
        val amount = Amount(12599L) // Represents 125.99
        assertEquals("125.99", amount.toFixed(2))
        assertEquals("126.0", amount.toFixed(1))
        assertEquals("126", amount.toFixed(0))

        // Test with an amount that will round down
        val amount2 = Amount(12544L) // Represents 125.44
        assertEquals("125.44", amount2.toFixed(2))
        assertEquals("125.4", amount2.toFixed(1))
        assertEquals("125", amount2.toFixed(0))
    }
}
