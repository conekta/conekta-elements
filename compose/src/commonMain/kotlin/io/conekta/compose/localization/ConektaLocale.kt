package io.conekta.compose.localization

import androidx.compose.runtime.Composable
import io.conekta.elements.localization.normalizeConektaLanguageTag

internal fun normalizeLanguageTag(languageTag: String?): String = normalizeConektaLanguageTag(languageTag)

@Composable
internal expect fun rememberDeviceLanguageTag(): String

@Composable
internal expect fun ProvideLanguage(
    languageTag: String,
    content: @Composable () -> Unit,
)
