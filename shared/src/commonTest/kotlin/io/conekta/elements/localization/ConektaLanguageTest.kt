package io.conekta.elements.localization

import kotlin.test.Test
import kotlin.test.assertEquals

class ConektaLanguageTest {
    @Test
    fun normalizeLanguageTag_returnsEnglish_forEnglishVariants() {
        assertEquals(ConektaLanguage.EN, normalizeConektaLanguageTag("en"))
        assertEquals(ConektaLanguage.EN, normalizeConektaLanguageTag("en-US"))
        assertEquals(ConektaLanguage.EN, normalizeConektaLanguageTag("EN_gb"))
    }

    @Test
    fun normalizeLanguageTag_returnsSpanish_forSpanishVariants() {
        assertEquals(ConektaLanguage.ES, normalizeConektaLanguageTag("es"))
        assertEquals(ConektaLanguage.ES, normalizeConektaLanguageTag("es-419"))
        assertEquals(ConektaLanguage.ES, normalizeConektaLanguageTag("ES_mx"))
    }

    @Test
    fun normalizeLanguageTag_returnsSpanish_forNullBlankOrUnsupported() {
        assertEquals(ConektaLanguage.ES, normalizeConektaLanguageTag(null))
        assertEquals(ConektaLanguage.ES, normalizeConektaLanguageTag(""))
        assertEquals(ConektaLanguage.ES, normalizeConektaLanguageTag("fr"))
    }
}
