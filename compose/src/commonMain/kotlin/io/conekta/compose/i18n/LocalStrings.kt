package io.conekta.compose.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.lyricist.Lyricist
import cafe.adriel.lyricist.ProvideStrings

/**
 * Lyricist instance for internationalization
 */
val lyricist = Lyricist(
    defaultLanguageTag = Language.ES.code,
    translations = mapOf(
        Language.ES.code to StringsEs,
        Language.EN.code to StringsEn
    )
)

/**
 * CompositionLocal for accessing strings
 */
val LocalStrings = staticCompositionLocalOf { StringsEs }

/**
 * Composable wrapper to provide strings to the composition tree
 */
@Composable
fun ProvideConektaStrings(
    language: Language = Language.ES,
    content: @Composable () -> Unit
) {
    lyricist.languageTag = language.code
    ProvideStrings(lyricist, LocalStrings, content)
}

/**
 * Get current strings from the composition
 */
val strings: Strings
    @Composable
    get() = LocalStrings.current
