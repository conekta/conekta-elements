package io.conekta.compose.checkout

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.conekta.compose.components.CheckoutContent
import io.conekta.compose.localization.ProvideLanguage
import io.conekta.compose.localization.normalizeLanguageTag
import io.conekta.compose.localization.rememberDeviceLanguageTag
import io.conekta.compose.theme.ConektaTheme
import io.conekta.compose.utils.colorFromHex
import io.conekta.elements.checkout.api.CheckoutApiService
import io.conekta.elements.checkout.models.CheckoutConfig
import io.conekta.elements.checkout.models.CheckoutError
import io.conekta.elements.checkout.models.CheckoutOrderResult
import io.conekta.elements.resources.CDNResources

private const val AUTO_LANGUAGE_TAG = "auto"

@Composable
fun ConektaCheckout(
    config: CheckoutConfig,
    onPaymentMethodSelected: (String) -> Unit,
    onError: (CheckoutError) -> Unit,
    modifier: Modifier = Modifier,
    onLanguageChanged: ((String) -> Unit)? = null,
    onOrderCreated: ((CheckoutOrderResult) -> Unit)? = null,
    checkoutApiServiceFactory: (CheckoutConfig) -> CheckoutApiService = { CheckoutApiService(it) },
) {
    val deviceLanguage = rememberDeviceLanguageTag()
    val requestedLanguageTag =
        config.languageTag.takeUnless { it.equals(AUTO_LANGUAGE_TAG, ignoreCase = true) } ?: deviceLanguage
    var currentLanguageTag by remember(config.languageTag, deviceLanguage) {
        mutableStateOf(
            normalizeLanguageTag(requestedLanguageTag),
        )
    }

    ConektaTheme {
        ProvideLanguage(languageTag = currentLanguageTag) {
            key(currentLanguageTag) {
                Surface(
                    modifier = modifier.fillMaxWidth(),
                    color = colorFromHex(CDNResources.Colors.CHECKOUT_BACKGROUND),
                ) {
                    val localizedConfig = config.copy(languageTag = currentLanguageTag)
                    CheckoutContent(
                        config = localizedConfig,
                        onPaymentMethodSelected = onPaymentMethodSelected,
                        onError = onError,
                        onOrderCreated = onOrderCreated,
                        currentLanguageTag = currentLanguageTag,
                        onLanguageSelected = { selectedLanguageTag ->
                            currentLanguageTag = normalizeLanguageTag(selectedLanguageTag)
                            onLanguageChanged?.invoke(currentLanguageTag)
                        },
                        checkoutApiServiceFactory = checkoutApiServiceFactory,
                    )
                }
            }
        }
    }
}
