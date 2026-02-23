package io.conekta.compose.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

private const val APPLE_LANGUAGES_KEY = "AppleLanguages"
private val LocalAppLocale = staticCompositionLocalOf { defaultLocale() }

private fun defaultLocale(): String {
    return normalizeLanguageTag(NSLocale.currentLocale.languageCode)
}

@Composable
internal actual fun rememberDeviceLanguageTag(): String = normalizeLanguageTag(LocalAppLocale.current)

@Composable
internal actual fun ProvideLanguage(
    languageTag: String,
    content: @Composable () -> Unit,
) {
    val normalizedLanguage = normalizeLanguageTag(languageTag)
    NSUserDefaults.standardUserDefaults.setObject(
        value = listOf(normalizedLanguage),
        forKey = APPLE_LANGUAGES_KEY,
    )

    CompositionLocalProvider(
        LocalAppLocale.providesLocale(normalizedLanguage),
    ) {
        content()
    }
}

private fun androidx.compose.runtime.ProvidableCompositionLocal<String>.providesLocale(value: String): ProvidedValue<String> =
    this.provides(value)
