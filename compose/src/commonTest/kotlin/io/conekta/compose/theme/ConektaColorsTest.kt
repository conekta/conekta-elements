package io.conekta.compose.theme

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ConektaColorsTest {
    @Test
    fun `Surface color is correct`() {
        assertEquals(Color(0xFFFDFEFF), ConektaColors.Surface)
    }

    @Test
    fun `DarkIndigo color is correct`() {
        assertEquals(Color(0xFF081133), ConektaColors.DarkIndigo)
    }

    @Test
    fun `Neutral8 color is correct`() {
        assertEquals(Color(0xFF585987), ConektaColors.Neutral8)
    }

    @Test
    fun `Neutral7 color is correct`() {
        assertEquals(Color(0xFF8D8FBA), ConektaColors.Neutral7)
    }

    @Test
    fun `Neutral5 color is correct`() {
        assertEquals(Color(0xFFD8D8E8), ConektaColors.Neutral5)
    }

    @Test
    fun `Neutral4 color is correct`() {
        assertEquals(Color(0xFFE7E8F4), ConektaColors.Neutral4)
    }

    @Test
    fun `CoreIndigo color is correct`() {
        assertEquals(Color(0xFF171D4D), ConektaColors.CoreIndigo)
    }

    @Test
    fun `Pearl100 color is correct`() {
        assertEquals(Color(0xFFF7F9FF), ConektaColors.Pearl100)
    }

    @Test
    fun `Shadow color is correct`() {
        assertEquals(Color(0x292C4CF5), ConektaColors.Shadow)
    }

    @Test
    fun `Error color is red`() {
        assertEquals(Color(0xFFDC2626), ConektaColors.Error)
    }

    @Test
    fun `all colors are distinct`() {
        val colors = listOf(
            ConektaColors.Surface,
            ConektaColors.DarkIndigo,
            ConektaColors.Neutral8,
            ConektaColors.Neutral7,
            ConektaColors.Neutral5,
            ConektaColors.Neutral4,
            ConektaColors.CoreIndigo,
            ConektaColors.Pearl100,
            ConektaColors.Error,
        )
        val uniqueColors = colors.toSet()
        assertEquals(colors.size, uniqueColors.size)
    }

    @Test
    fun `DarkIndigo is different from CoreIndigo`() {
        assertNotEquals(ConektaColors.DarkIndigo, ConektaColors.CoreIndigo)
    }

    @Test
    fun `Surface is near white`() {
        val surfaceRed = ConektaColors.Surface.red
        val surfaceGreen = ConektaColors.Surface.green
        val surfaceBlue = ConektaColors.Surface.blue
        // Surface should be very light (near white)
        assertTrue(surfaceRed > 0.9f)
        assertTrue(surfaceGreen > 0.9f)
        assertTrue(surfaceBlue > 0.9f)
    }
}
