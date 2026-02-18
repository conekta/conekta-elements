package io.conekta.compose.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.inter_bold
import io.conekta.compose.generated.resources.inter_medium
import io.conekta.compose.generated.resources.inter_regular
import io.conekta.compose.generated.resources.inter_semibold
import org.jetbrains.compose.resources.Font

val LocalConektaFontFamily = staticCompositionLocalOf<FontFamily> { FontFamily.Default }

private val ConektaLightColorScheme =
    lightColorScheme(
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
        outline = ConektaColors.Neutral5,
    )

/**
 * Conekta theme wrapper for Material3
 * Applies Conekta design system colors and Inter typography
 */
@Composable
fun ConektaTheme(content: @Composable () -> Unit) {
    val interFamily =
        FontFamily(
            Font(Res.font.inter_regular, FontWeight.Normal),
            Font(Res.font.inter_medium, FontWeight.Medium),
            Font(Res.font.inter_semibold, FontWeight.SemiBold),
            Font(Res.font.inter_bold, FontWeight.Bold),
        )

    val defaultTypography = Typography()
    val conektaTypography =
        Typography(
            displayLarge = defaultTypography.displayLarge.copy(fontFamily = interFamily),
            displayMedium = defaultTypography.displayMedium.copy(fontFamily = interFamily),
            displaySmall = defaultTypography.displaySmall.copy(fontFamily = interFamily),
            headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = interFamily),
            headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = interFamily),
            headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = interFamily),
            titleLarge = defaultTypography.titleLarge.copy(fontFamily = interFamily),
            titleMedium = defaultTypography.titleMedium.copy(fontFamily = interFamily),
            titleSmall = defaultTypography.titleSmall.copy(fontFamily = interFamily),
            bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = interFamily),
            bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = interFamily),
            bodySmall = defaultTypography.bodySmall.copy(fontFamily = interFamily),
            labelLarge = defaultTypography.labelLarge.copy(fontFamily = interFamily),
            labelMedium = defaultTypography.labelMedium.copy(fontFamily = interFamily),
            labelSmall = defaultTypography.labelSmall.copy(fontFamily = interFamily),
        )

    CompositionLocalProvider(LocalConektaFontFamily provides interFamily) {
        MaterialTheme(
            colorScheme = ConektaLightColorScheme,
            typography = conektaTypography,
            content = content,
        )
    }
}
