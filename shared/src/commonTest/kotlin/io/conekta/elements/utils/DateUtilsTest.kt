package io.conekta.elements.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class DateUtilsTest {
    @Test
    fun `formatEpochSecondsInMexicoCity formats unix epoch start`() {
        val (date, time) = formatEpochSecondsInMexicoCity(epochSeconds = 0L)

        assertEquals("31/12/1969", date)
        assertEquals("06:00 pm", time)
    }

    @Test
    fun `formatEpochSecondsInMexicoCity formats midnight correctly`() {
        val (date, time) = formatEpochSecondsInMexicoCity(epochSeconds = 21600L)

        assertEquals("01/01/1970", date)
        assertEquals("12:00 am", time)
    }

    @Test
    fun `formatEpochSecondsInMexicoCity supports negative epoch values`() {
        val (date, time) = formatEpochSecondsInMexicoCity(epochSeconds = -1L)

        assertEquals("31/12/1969", date)
        assertEquals("05:59 pm", time)
    }

    @Test
    fun `formatEpochSecondsInMexicoCity handles leap year calendar conversion`() {
        // 2024-03-01 00:00:00 UTC -> 2024-02-29 18:00:00 in fixed UTC-6.
        val (date, time) = formatEpochSecondsInMexicoCity(epochSeconds = 1709251200L)

        assertEquals("29/02/2024", date)
        assertEquals("06:00 pm", time)
    }
}
