package io.conekta.elements.localization

object ConektaLanguage {
    const val ES = "es"
    const val EN = "en"
}

fun normalizeConektaLanguageTag(languageTag: String?): String {
    if (languageTag.isNullOrBlank()) return ConektaLanguage.ES

    val normalized = languageTag.lowercase()
    return when {
        normalized.startsWith(ConektaLanguage.EN) -> ConektaLanguage.EN
        normalized.startsWith(ConektaLanguage.ES) -> ConektaLanguage.ES
        else -> ConektaLanguage.ES
    }
}
