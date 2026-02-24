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
import androidx.compose.ui.text.input.TextFieldValue
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

internal class CardFieldsState {
    var cardholderName by mutableStateOf(TextFieldValue(""))
    var cardNumber by mutableStateOf(TextFieldValue(""))
    var expiryDate by mutableStateOf(TextFieldValue(""))
    var cvv by mutableStateOf(TextFieldValue(""))
    var cardholderNameError by mutableStateOf(false)
    var cardNumberError by mutableStateOf(false)
    var expiryDateError by mutableStateOf(false)
    var cvvError by mutableStateOf(false)
    var cardholderNameErrorMsg by mutableStateOf<String?>(null)
    var cardNumberErrorMsg by mutableStateOf<String?>(null)
    var expiryDateErrorMsg by mutableStateOf<String?>(null)
    var cvvErrorMsg by mutableStateOf<String?>(null)
}

private const val AUTO_LANGUAGE_TAG = "auto"
private val CheckoutBg = colorFromHex(CDNResources.Colors.CHECKOUT_BACKGROUND)

@Composable
fun ConektaCheckout(
    config: CheckoutConfig,
    onPaymentMethodSelected: (String) -> Unit,
    onError: (CheckoutError) -> Unit,
    modifier: Modifier = Modifier,
    initialLanguageTag: String = AUTO_LANGUAGE_TAG,
    onLanguageChanged: ((String) -> Unit)? = null,
    onOrderCreated: ((CheckoutOrderResult) -> Unit)? = null,
    checkoutApiServiceFactory: (CheckoutConfig) -> CheckoutApiService = { CheckoutApiService(it) },
) {
    val deviceLanguage = rememberDeviceLanguageTag()
    var currentLanguageTag by remember(initialLanguageTag, deviceLanguage) {
        mutableStateOf(
            if (initialLanguageTag == AUTO_LANGUAGE_TAG) {
                normalizeLanguageTag(deviceLanguage)
            } else {
                normalizeLanguageTag(initialLanguageTag)
            },
        )
    }

    ConektaTheme {
        ProvideLanguage(languageTag = currentLanguageTag) {
            key(currentLanguageTag) {
                Surface(
                    modifier = modifier.fillMaxWidth(),
                    color = CheckoutBg,
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
