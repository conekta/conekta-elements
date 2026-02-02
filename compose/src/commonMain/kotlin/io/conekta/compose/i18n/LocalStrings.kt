package io.conekta.compose.i18n

import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.lyricist.Lyricist
import cafe.adriel.lyricist.ProvideStrings
import cafe.adriel.lyricist.rememberStrings

/**
 * Lyricist instance for internationalization
 */
val lyricist = Lyricist(Language.ES, Strings::languageTag) {
    language(Language.ES, StringsEs)
    language(Language.EN, StringsEn)
}

/**
 * CompositionLocal for accessing strings
 */
val LocalStrings = staticCompositionLocalOf { StringsEs }

/**
 * Composable wrapper to provide strings to the composition tree
 */
fun provideStrings(
    languageTag: Language = Language.ES,
    content: @androidx.compose.runtime.Composable () -> Unit
) {
    ProvideStrings(lyricist, languageTag, LocalStrings, content)
}

/**
 * Remember strings hook for accessing current strings
 */
@androidx.compose.runtime.Composable
fun strings(): Strings = rememberStrings()
