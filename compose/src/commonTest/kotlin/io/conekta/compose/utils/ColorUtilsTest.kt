package io.conekta.compose.utils

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ColorUtilsTest {
    @Test
    fun colorFromHexParsesThreeDigitHex() {
        val color = colorFromHex("#ABC")
        assertEquals(Color(0xFFAABBCC), color)
    }

    @Test
    fun colorFromHexParsesSixDigitHexWithoutHash() {
        val color = colorFromHex("112233")
        assertEquals(Color(0xFF112233), color)
    }

    @Test
    fun colorFromHexParsesEightDigitArgbHex() {
        val color = colorFromHex("80112233")
        assertEquals(Color(0x80112233), color)
    }

    @Test
    fun colorFromHexTrimsInputBeforeParsing() {
        val color = colorFromHex("  #0F0  ")
        assertEquals(Color(0xFF00FF00), color)
    }

    @Test
    fun colorFromHexFailsForUnsupportedLength() {
        assertFailsWith<IllegalStateException> {
            colorFromHex("#12")
        }
    }
}
