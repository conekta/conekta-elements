package io.conekta.compose.localization

import android.content.res.Configuration
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
internal actual fun rememberDeviceLanguageTag(): String {
    val configuration = LocalConfiguration.current
    val deviceLocale =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.locales.get(0)
        } else {
            @Suppress("DEPRECATION")
            configuration.locale
        }
    return normalizeLanguageTag(deviceLocale?.language)
}

@Composable
internal actual fun ProvideLanguage(
    languageTag: String,
    content: @Composable () -> Unit,
) {
    val baseContext = LocalContext.current
    val targetLocale = Locale.forLanguageTag(normalizeLanguageTag(languageTag))
    Locale.setDefault(targetLocale)
    val localizedConfiguration =
        Configuration(baseContext.resources.configuration).apply {
            setLocale(targetLocale)
        }
    val localizedContext = baseContext.createConfigurationContext(localizedConfiguration)

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedConfiguration,
    ) {
        content()
    }
}
