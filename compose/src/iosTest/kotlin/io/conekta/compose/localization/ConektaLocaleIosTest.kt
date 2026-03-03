package io.conekta.compose.localization

import kotlin.test.Test
import kotlin.test.assertEquals

class ConektaLocaleIosTest {
    @Test
    fun resolvedDeviceLanguageTagReturnsSpanishByDefaultForNull() {
        assertEquals("es", resolvedDeviceLanguageTag(null))
    }

    @Test
    fun resolvedDeviceLanguageTagNormalizesEnglishVariants() {
        assertEquals("en", resolvedDeviceLanguageTag("EN"))
        assertEquals("en", resolvedDeviceLanguageTag("en-US"))
    }

    @Test
    fun resolvedDeviceLanguageTagNormalizesSpanishVariants() {
        assertEquals("es", resolvedDeviceLanguageTag("ES"))
        assertEquals("es", resolvedDeviceLanguageTag("es-MX"))
    }

    @Test
    fun resolvedDeviceLanguageTagFallsBackToSpanishForUnsupportedLanguage() {
        assertEquals("es", resolvedDeviceLanguageTag("fr"))
    }
}
