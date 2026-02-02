package io.conekta.compose.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ConektaLightColorScheme = lightColorScheme(
    primary = ConektaColors.CoreIndigo,
    onPrimary = ConektaColors.Pearl100,
    primaryContainer = ConektaColors.Neutral4,
    onPrimaryContainer = ConektaColors.DarkIndigo,
    secondary = ConektaColors.Neutral7,
    onSecondary = Color.White,
    surface = ConektaColors.Surface,
    onSurface = ConektaColors.DarkIndigo,
    background = Color.White,
    onBackground = ConektaColors.DarkIndigo,
    outline = ConektaColors.Neutral5
)

/**
 * Conekta theme wrapper for Material3
 * Applies Conekta design system colors and typography
 */
@Composable
fun ConektaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ConektaLightColorScheme,
        content = content
    )
}

