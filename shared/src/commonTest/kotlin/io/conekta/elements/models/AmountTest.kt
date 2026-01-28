package io.conekta.elements.models

import kotlin.test.Test
import kotlin.test.assertEquals

class AmountTest {
    @Test
    fun `apiFormat divides by 100`() {
        val amount = Amount(12599.0)
        assertEquals(125.99, amount.apiFormat())
    }

    @Test
    fun `toFixed formats with correct decimals`() {
        val amount = Amount(12599.456)
        assertEquals("12599.46", amount.toFixed(2))
        assertEquals("12599.5", amount.toFixed(1))
        assertEquals("12599", amount.toFixed(0))
    }
}
