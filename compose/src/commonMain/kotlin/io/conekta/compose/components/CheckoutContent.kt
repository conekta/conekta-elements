package io.conekta.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import io.conekta.compose.checkout.CardFieldsState
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_button_pay
import io.conekta.compose.generated.resources.checkout_empty_methods
import io.conekta.compose.generated.resources.checkout_validation_checkout_request_id_required
import io.conekta.compose.generated.resources.checkout_validation_jwt_token_required
import io.conekta.compose.generated.resources.checkout_validation_public_key_required
import io.conekta.compose.generated.resources.error_field_required
import io.conekta.compose.generated.resources.validation_card_min_length
import io.conekta.compose.generated.resources.validation_cvv_min_length
import io.conekta.compose.generated.resources.validation_expiry_year_invalid
import io.conekta.compose.generated.resources.validation_invalid_card
import io.conekta.compose.utils.colorFromHex
import io.conekta.elements.checkout.api.CheckoutApiException
import io.conekta.elements.checkout.api.CheckoutApiService
import io.conekta.elements.checkout.models.CheckoutConfig
import io.conekta.elements.checkout.models.CheckoutConfigValidationMessages
import io.conekta.elements.checkout.models.CheckoutConfigValidator
import io.conekta.elements.checkout.models.CheckoutError
import io.conekta.elements.checkout.models.CheckoutMethodPolicy
import io.conekta.elements.checkout.models.CheckoutOrderResult
import io.conekta.elements.checkout.models.CheckoutPaymentMethod
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.models.Amount
import io.conekta.elements.resources.CDNResources
import io.conekta.elements.tokenizer.api.TokenizerApiService
import io.conekta.elements.tokenizer.models.TokenizerConfig
import io.conekta.elements.tokenizer.validators.ValidationMessages
import io.conekta.elements.utils.currentTwoDigitYear
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private val CheckoutBg = colorFromHex(CDNResources.Colors.CHECKOUT_BACKGROUND)

@Composable
internal fun CheckoutContent(
    config: CheckoutConfig,
    onPaymentMethodSelected: (String) -> Unit,
    onError: (CheckoutError) -> Unit,
    onOrderCreated: ((CheckoutOrderResult) -> Unit)?,
    currentLanguageTag: String,
    onLanguageSelected: (String) -> Unit,
    checkoutApiServiceFactory: (CheckoutConfig) -> CheckoutApiService,
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val checkoutService = remember(config) { checkoutApiServiceFactory(config) }
    val tokenizerService =
        remember(config.publicKey, config.languageTag, config.tokenizerBaseUrl, config.tokenizerRsaPublicKey) {
            val tokenizerRsaPublicKey = config.tokenizerRsaPublicKey
            val tokenizerConfig =
                if (tokenizerRsaPublicKey.isNullOrBlank()) {
                    TokenizerConfig(
                        publicKey = config.publicKey,
                        baseUrl = config.tokenizerBaseUrl,
                    )
                } else {
                    TokenizerConfig(
                        publicKey = config.publicKey,
                        baseUrl = config.tokenizerBaseUrl,
                        rsaPublicKey = tokenizerRsaPublicKey,
                    )
                }
            TokenizerApiService(
                config = tokenizerConfig,
                languageTag = config.languageTag,
            )
        }

    val checkoutEmptyMethodsMessage = stringResource(Res.string.checkout_empty_methods)
    val checkoutRequestIdRequiredMessage = stringResource(Res.string.checkout_validation_checkout_request_id_required)
    val publicKeyRequiredMessage = stringResource(Res.string.checkout_validation_public_key_required)
    val jwtTokenRequiredMessage = stringResource(Res.string.checkout_validation_jwt_token_required)
    val requiredFieldMessage = stringResource(Res.string.error_field_required)
    val cardMinLengthMessage = stringResource(Res.string.validation_card_min_length)
    val invalidCardMessage = stringResource(Res.string.validation_invalid_card)
    val minimumYearLabel = currentTwoDigitYear().toString().padStart(2, '0')
    val expiryYearInvalidMessage = stringResource(Res.string.validation_expiry_year_invalid, minimumYearLabel)
    val cvvMinLengthMessage = stringResource(Res.string.validation_cvv_min_length)

    var isLoading by remember(config.checkoutRequestId) { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var checkoutResult by remember(config.checkoutRequestId) { mutableStateOf<CheckoutResult?>(null) }
    var selectedPaymentMethod by remember { mutableStateOf<String?>(null) }
    var showProtectionSheet by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var submitErrorToastMessage by remember { mutableStateOf<String?>(null) }

    val cardFields = remember { CardFieldsState() }
    val cardValidationMessages =
        remember(
            requiredFieldMessage,
            cardMinLengthMessage,
            invalidCardMessage,
            expiryYearInvalidMessage,
            cvvMinLengthMessage,
        ) {
            ValidationMessages(
                required = requiredFieldMessage,
                cardMinLength = cardMinLengthMessage,
                invalidCard = invalidCardMessage,
                expiryYearInvalid = expiryYearInvalidMessage,
                cvvMinLength = cvvMinLengthMessage,
            )
        }

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

    Box(modifier = Modifier.fillMaxWidth()) {
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

            CheckoutMethodSectionTitle(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 10.dp),
            )

            PaymentMethodsSection(
                methods = CheckoutMethodPolicy.filterSupportedMethods(checkoutResult?.allowedPaymentMethods.orEmpty()),
                selectedPaymentMethod = selectedPaymentMethod,
                onMethodSelected = {
                    selectedPaymentMethod = it
                    onPaymentMethodSelected(it)
                },
                isLoading = isLoading,
                loadError = loadError,
                cardFields = cardFields,
                cashProviders = checkoutResult?.providers.orEmpty(),
                cardValidationMessages = cardValidationMessages,
            )

            ConektaButton(
                text =
                    checkoutResult?.let {
                        "${stringResource(Res.string.checkout_button_pay)} $${Amount(it.amount.toInt()).toFixed(2)}"
                    } ?: stringResource(Res.string.checkout_button_pay),
                onClick = {
                    val methodKey = selectedPaymentMethod ?: return@ConektaButton
                    val input =
                        CheckoutPaymentMethodValidationInput(
                            cardFields = cardFields,
                            cardValidationMessages = cardValidationMessages,
                        )
                    val validator = CheckoutPaymentMethodValidators.forMethod(methodKey)
                    if (!validator.validateBeforeSubmit(input)) return@ConektaButton
                    isSubmitting = true
                    coroutineScope.launch {
                        submitOrder(
                            methodKey = methodKey,
                            tokenizerService = tokenizerService,
                            checkoutService = checkoutService,
                            cardNumber = cardFields.cardNumber.text,
                            expiryDate = cardFields.expiryDate.text,
                            cvv = cardFields.cvv.text,
                            cardholderName = cardFields.cardholderName.text,
                            onOrderCreated = onOrderCreated,
                            onError = onError,
                            onSubmitError = { submitErrorToastMessage = it },
                        )
                        isSubmitting = false
                    }
                },
                enabled =
                    selectedPaymentMethod?.let { methodKey ->
                        CheckoutPaymentMethodValidators
                            .forMethod(methodKey)
                            .canSubmit(
                                CheckoutPaymentMethodValidationInput(
                                    cardFields = cardFields,
                                    cardValidationMessages = cardValidationMessages,
                                ),
                            ) && !isSubmitting
                    } ?: false,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                height = 56,
            )

            CheckoutFooter(
                selectedLanguageTag = currentLanguageTag,
                onLanguageSelected = onLanguageSelected,
            )
        }

        submitErrorToastMessage?.let { errorMessage ->
            ConektaErrorToast(
                message = errorMessage,
                onDismiss = { submitErrorToastMessage = null },
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }
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

private suspend fun submitOrder(
    methodKey: String,
    tokenizerService: TokenizerApiService,
    checkoutService: CheckoutApiService,
    cardNumber: String,
    expiryDate: String,
    cvv: String,
    cardholderName: String,
    onOrderCreated: ((CheckoutOrderResult) -> Unit)?,
    onError: (CheckoutError) -> Unit,
    onSubmitError: (String) -> Unit,
) {
    try {
        val method =
            CheckoutPaymentMethod.from(
                methodKey = methodKey,
                tokenizerService = tokenizerService,
                cardNumber = cardNumber,
                expiryDate = expiryDate,
                cvv = cvv,
                cardholderName = cardholderName,
            )
        val tokenId = method.resolveTokenId()

        checkoutService
            .createOrder(method.methodKey, tokenId = tokenId)
            .onSuccess { order -> onOrderCreated?.invoke(order) }
            .onFailure { e ->
                val error =
                    (e as? CheckoutApiException)?.checkoutError
                        ?: CheckoutError.NetworkError(e.message ?: "Unknown error")
                onError(error)
                onSubmitError(errorMessage(error))
            }
    } catch (e: CheckoutApiException) {
        onError(e.checkoutError)
        onSubmitError(errorMessage(e.checkoutError))
    } catch (e: Exception) {
        val error = CheckoutError.NetworkError(e.message ?: "Unknown error")
        onError(error)
        onSubmitError(error.message)
    }
}

private fun errorMessage(error: CheckoutError): String =
    when (error) {
        is CheckoutError.ApiError -> error.message
        is CheckoutError.NetworkError -> error.message
        is CheckoutError.ValidationError -> error.message
    }
