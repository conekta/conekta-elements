package io.conekta.compose.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.conekta.compose.components.CheckoutBankTransferMethodItem
import io.conekta.compose.components.CheckoutBankTransferMethodSection
import io.conekta.compose.components.CheckoutCardMethodItem
import io.conekta.compose.components.CheckoutCardMethodSection
import io.conekta.compose.components.CheckoutCashMethodItem
import io.conekta.compose.components.CheckoutCashMethodSection
import io.conekta.compose.components.CheckoutFooter
import io.conekta.compose.components.CheckoutHeader
import io.conekta.compose.components.CheckoutTotalRow
import io.conekta.compose.components.ConektaButton
import io.conekta.compose.components.PaymentProtectionSheet
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_button_pay
import io.conekta.compose.generated.resources.checkout_empty_methods
import io.conekta.compose.generated.resources.checkout_error_loading
import io.conekta.compose.generated.resources.checkout_loading
import io.conekta.compose.generated.resources.checkout_method_title
import io.conekta.compose.generated.resources.checkout_validation_checkout_request_id_required
import io.conekta.compose.generated.resources.checkout_validation_jwt_token_required
import io.conekta.compose.generated.resources.checkout_validation_public_key_required
import io.conekta.compose.generated.resources.placeholder_cardholder_name_checkout
import io.conekta.compose.localization.ProvideLanguage
import io.conekta.compose.localization.normalizeLanguageTag
import io.conekta.compose.localization.rememberDeviceLanguageTag
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.theme.ConektaTheme
import io.conekta.compose.theme.LocalConektaFontFamily
import io.conekta.compose.utils.colorFromHex
import io.conekta.elements.checkout.api.CheckoutApiException
import io.conekta.elements.checkout.api.CheckoutApiService
import io.conekta.elements.checkout.models.CheckoutConfig
import io.conekta.elements.checkout.models.CheckoutConfigValidationMessages
import io.conekta.elements.checkout.models.CheckoutConfigValidator
import io.conekta.elements.checkout.models.CheckoutError
import io.conekta.elements.checkout.models.CheckoutMethodPolicy
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.checkout.models.CheckoutProvider
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.models.Amount
import io.conekta.elements.resources.CDNResources
import org.jetbrains.compose.resources.stringResource

private const val AUTO_LANGUAGE_TAG = "auto"
private val CheckoutBg = colorFromHex(CDNResources.Colors.CHECKOUT_BACKGROUND)
private val Border = colorFromHex(CDNResources.Colors.CHECKOUT_BORDER)
private val OnSurface = colorFromHex(CDNResources.Colors.CHECKOUT_ON_SURFACE)

private object CheckoutResultCache {
    private val cache = mutableMapOf<String, CheckoutResult>()

    fun get(checkoutRequestId: String): CheckoutResult? = cache[checkoutRequestId]

    fun put(
        checkoutRequestId: String,
        result: CheckoutResult,
    ) {
        cache[checkoutRequestId] = result
    }
}

@Composable
fun ConektaCheckout(
    config: CheckoutConfig,
    onPaymentMethodSelected: (String) -> Unit,
    onError: (CheckoutError) -> Unit,
    modifier: Modifier = Modifier,
    initialLanguageTag: String = AUTO_LANGUAGE_TAG,
    onLanguageChanged: ((String) -> Unit)? = null,
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
                    CheckoutContent(
                        config = config,
                        onPaymentMethodSelected = onPaymentMethodSelected,
                        onError = onError,
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

@Composable
private fun CheckoutContent(
    config: CheckoutConfig,
    onPaymentMethodSelected: (String) -> Unit,
    onError: (CheckoutError) -> Unit,
    currentLanguageTag: String,
    onLanguageSelected: (String) -> Unit,
    checkoutApiServiceFactory: (CheckoutConfig) -> CheckoutApiService,
) {
    val focusManager = LocalFocusManager.current
    val checkoutService = remember(config) { checkoutApiServiceFactory(config) }

    val checkoutEmptyMethodsMessage = stringResource(Res.string.checkout_empty_methods)
    val checkoutRequestIdRequiredMessage = stringResource(Res.string.checkout_validation_checkout_request_id_required)
    val publicKeyRequiredMessage = stringResource(Res.string.checkout_validation_public_key_required)
    val jwtTokenRequiredMessage = stringResource(Res.string.checkout_validation_jwt_token_required)

    var isLoading by remember(config.checkoutRequestId) { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var checkoutResult by remember(config.checkoutRequestId) { mutableStateOf(CheckoutResultCache.get(config.checkoutRequestId)) }
    var selectedPaymentMethod by remember { mutableStateOf<String?>(null) }
    var showProtectionSheet by remember { mutableStateOf(false) }

    var cardholderName by remember { mutableStateOf(TextFieldValue("")) }
    var cardNumber by remember { mutableStateOf(TextFieldValue("")) }
    var expiryDate by remember { mutableStateOf(TextFieldValue("")) }
    var cvv by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(config) {
        val validationError =
            CheckoutConfigValidator.validate(
                config = config,
                messages =
                    CheckoutConfigValidationMessages(
                        checkoutRequestIdRequired = checkoutRequestIdRequiredMessage,
                        publicKeyRequired = publicKeyRequiredMessage,
                        jwtTokenRequired = jwtTokenRequiredMessage,
                    ),
            )

        if (validationError != null) {
            onError(validationError)
            loadError = validationError.message
            isLoading = false
            return@LaunchedEffect
        }

        if (checkoutResult != null) {
            val defaultMethod =
                CheckoutMethodPolicy.selectDefaultSupportedMethod(checkoutResult?.allowedPaymentMethods.orEmpty())
            if (selectedPaymentMethod == null && defaultMethod != null) {
                selectedPaymentMethod = defaultMethod
                onPaymentMethodSelected(defaultMethod)
            }
            isLoading = false
            return@LaunchedEffect
        }

        isLoading = true
        loadError = null

        checkoutService
            .fetchCheckout()
            .onSuccess { checkout ->
                checkoutResult = checkout
                CheckoutResultCache.put(config.checkoutRequestId, checkout)
                val defaultMethod =
                    CheckoutMethodPolicy.selectDefaultSupportedMethod(checkout.allowedPaymentMethods)
                if (defaultMethod == null) {
                    val error = CheckoutError.ValidationError(checkoutEmptyMethodsMessage)
                    onError(error)
                    loadError = error.message
                    selectedPaymentMethod = null
                } else {
                    selectedPaymentMethod = defaultMethod
                    onPaymentMethodSelected(defaultMethod)
                }
            }.onFailure { throwable ->
                val checkoutError =
                    when (throwable) {
                        is CheckoutApiException -> throwable.checkoutError
                        else -> CheckoutError.NetworkError(throwable.message ?: "Unknown network error")
                    }
                onError(checkoutError)
                loadError =
                    when (checkoutError) {
                        is CheckoutError.ApiError -> checkoutError.message
                        is CheckoutError.NetworkError -> checkoutError.message
                        is CheckoutError.ValidationError -> checkoutError.message
                    }
            }

        isLoading = false
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(CheckoutBg)
                .verticalScroll(rememberScrollState())
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                },
    ) {
        CheckoutTotalRow(
            amountText = "$${Amount((checkoutResult?.amount ?: 0).toInt()).toFixed(2)}",
            lineItems = checkoutResult?.lineItems.orEmpty(),
            taxLines = checkoutResult?.taxLines.orEmpty(),
            discountLines = checkoutResult?.discountLines.orEmpty(),
            shippingLines = checkoutResult?.shippingLines.orEmpty(),
        )
        CheckoutHeader(onInfoClick = { showProtectionSheet = true })

        MethodSectionTitle()

        PaymentMethodsSection(
            methods = CheckoutMethodPolicy.filterSupportedMethods(checkoutResult?.allowedPaymentMethods.orEmpty()),
            selectedPaymentMethod = selectedPaymentMethod,
            onMethodSelected = {
                selectedPaymentMethod = it
                onPaymentMethodSelected(it)
            },
            isLoading = isLoading,
            loadError = loadError,
            cardholderName = cardholderName,
            cardNumber = cardNumber,
            expiryDate = expiryDate,
            cvv = cvv,
            cashProviders = checkoutResult?.providers.orEmpty(),
            onCardholderNameChange = { cardholderName = it },
            onCardNumberChange = { cardNumber = it },
            onExpiryDateChange = { expiryDate = it },
            onCvvChange = { cvv = it },
        )

        ConektaButton(
            text =
                checkoutResult?.let {
                    "${stringResource(Res.string.checkout_button_pay)} $${Amount(it.amount.toInt()).toFixed(2)}"
                } ?: stringResource(Res.string.checkout_button_pay),
            onClick = {},
            enabled = selectedPaymentMethod != null,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            height = 56,
        )

        CheckoutFooter(
            selectedLanguageTag = currentLanguageTag,
            onLanguageSelected = onLanguageSelected,
        )
    }

    if (showProtectionSheet) {
        PaymentProtectionSheet(
            merchantName = config.merchantName,
            onDismiss = {
                if (showProtectionSheet) {
                    showProtectionSheet = false
                }
            },
        )
    }
}

@Composable
private fun MethodSectionTitle() {
    val fontFamily = LocalConektaFontFamily.current
    Text(
        text = stringResource(Res.string.checkout_method_title),
        style =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 12.sp,
                color = OnSurface,
            ),
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 10.dp),
    )
}

@Composable
private fun PaymentMethodsSection(
    methods: List<String>,
    selectedPaymentMethod: String?,
    onMethodSelected: (String) -> Unit,
    isLoading: Boolean,
    loadError: String?,
    cardholderName: TextFieldValue,
    cardNumber: TextFieldValue,
    expiryDate: TextFieldValue,
    cvv: TextFieldValue,
    cashProviders: List<CheckoutProvider>,
    onCardholderNameChange: (TextFieldValue) -> Unit,
    onCardNumberChange: (TextFieldValue) -> Unit,
    onExpiryDateChange: (TextFieldValue) -> Unit,
    onCvvChange: (TextFieldValue) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
    ) {
        when {
            isLoading -> {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = ConektaColors.CoreIndigo)
                    Text(stringResource(Res.string.checkout_loading))
                }
            }
            loadError != null -> {
                Text(
                    text = loadError.ifBlank { stringResource(Res.string.checkout_error_loading) },
                    color = ConektaColors.Error,
                    modifier = Modifier.padding(16.dp),
                )
            }
            methods.isEmpty() -> {
                Text(
                    text = stringResource(Res.string.checkout_empty_methods),
                    color = ConektaColors.Neutral8,
                    modifier = Modifier.padding(16.dp),
                )
            }
            else -> {
                val selectedIndex = methods.indexOf(selectedPaymentMethod)
                Column {
                    methods.forEachIndexed { index, method ->
                        val selected = selectedPaymentMethod == method

                        when {
                            method == CheckoutPaymentMethods.CARD && selected -> {
                                Column(modifier = Modifier.zIndex(1f)) {
                                    CheckoutCardMethodSection(
                                        onSelect = { onMethodSelected(method) },
                                        cardholderName = cardholderName,
                                        onCardholderNameChange = onCardholderNameChange,
                                        cardNumber = cardNumber,
                                        onCardNumberChange = onCardNumberChange,
                                        expiryDate = expiryDate,
                                        onExpiryDateChange = onExpiryDateChange,
                                        cvv = cvv,
                                        onCvvChange = onCvvChange,
                                        cardholderNamePlaceholderOverride =
                                            stringResource(Res.string.placeholder_cardholder_name_checkout),
                                    )
                                }
                            }
                            method == CheckoutPaymentMethods.CARD -> {
                                CheckoutCardMethodItem(selected = false, onClick = { onMethodSelected(method) })
                            }
                            method == CheckoutPaymentMethods.CASH && selected -> {
                                Column(modifier = Modifier.zIndex(1f)) {
                                    CheckoutCashMethodSection(
                                        onSelect = { onMethodSelected(method) },
                                        providers = cashProviders,
                                    )
                                }
                            }
                            method == CheckoutPaymentMethods.CASH -> {
                                CheckoutCashMethodItem(selected = false, onClick = { onMethodSelected(method) })
                            }
                            method == CheckoutPaymentMethods.BANK_TRANSFER && selected -> {
                                Column(modifier = Modifier.zIndex(1f)) {
                                    CheckoutBankTransferMethodSection(
                                        onSelect = { onMethodSelected(method) },
                                    )
                                }
                            }
                            method == CheckoutPaymentMethods.BANK_TRANSFER -> {
                                CheckoutBankTransferMethodItem(
                                    selected = false,
                                    onClick = { onMethodSelected(method) },
                                )
                            }
                        }

                        val isDividerAdjacentToSelection = index == selectedIndex || index + 1 == selectedIndex
                        if (index < methods.lastIndex && !isDividerAdjacentToSelection) {
                            HorizontalDivider(color = Border)
                        }
                    }
                }
            }
        }
    }
}
