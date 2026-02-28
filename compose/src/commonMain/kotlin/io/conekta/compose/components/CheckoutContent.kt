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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import io.conekta.compose.components.banktransfer.BankTransferSuccessContent
import io.conekta.compose.components.card.CardFieldsState
import io.conekta.compose.components.card.CardSuccessContent
import io.conekta.compose.components.card.rememberCardValidationMessages
import io.conekta.compose.components.cash.CashSuccessContent
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_button_pay
import io.conekta.compose.generated.resources.checkout_empty_methods
import io.conekta.compose.generated.resources.checkout_validation_checkout_request_id_required
import io.conekta.compose.generated.resources.checkout_validation_jwt_token_required
import io.conekta.compose.generated.resources.checkout_validation_public_key_required
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
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.models.Amount
import io.conekta.elements.resources.CDNResources
import io.conekta.elements.tokenizer.api.TokenizerApiService
import io.conekta.elements.tokenizer.models.TokenizerConfig
import io.conekta.elements.tokenizer.validators.ValidationMessages
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

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
    val submitOrderScope = rememberCoroutineScope()
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

    var isLoading by remember(config.checkoutRequestId) { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var checkoutResult by remember(config.checkoutRequestId) { mutableStateOf<CheckoutResult?>(null) }
    var selectedPaymentMethod by remember { mutableStateOf<String?>(null) }
    var showProtectionSheet by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var submitErrorToastMessage by remember { mutableStateOf<String?>(null) }
    var orderResult by remember { mutableStateOf<CheckoutOrderResult?>(null) }

    val cardFields = remember { CardFieldsState() }
    val cardValidationMessages = rememberCardValidationMessages()

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

    val currentOrderResult = orderResult
    if (currentOrderResult != null && checkoutResult != null) {
        val shouldShowSuccess =
            when (selectedPaymentMethod) {
                CheckoutPaymentMethods.CASH,
                CheckoutPaymentMethods.BANK_TRANSFER,
                CheckoutPaymentMethods.CARD,
                -> true
                else -> false
            }
        if (shouldShowSuccess) {
            CheckoutSuccessShell(
                checkoutResult = checkoutResult!!,
                merchantName = config.merchantName,
                currentLanguageTag = currentLanguageTag,
                onLanguageSelected = onLanguageSelected,
            ) {
                when (selectedPaymentMethod) {
                    CheckoutPaymentMethods.CASH ->
                        CashSuccessContent(
                            orderResult = currentOrderResult,
                            checkoutResult = checkoutResult!!,
                        )
                    CheckoutPaymentMethods.BANK_TRANSFER ->
                        BankTransferSuccessContent(
                            orderResult = currentOrderResult,
                            checkoutResult = checkoutResult!!,
                        )
                    CheckoutPaymentMethods.CARD ->
                        CardSuccessContent(
                            checkoutResult = checkoutResult!!,
                            merchantName = config.merchantName,
                        )
                }
            }
            return
        }
    }

    CheckoutMainContent(
        checkoutResult = checkoutResult,
        selectedPaymentMethod = selectedPaymentMethod,
        onMethodSelected = {
            selectedPaymentMethod = it
            onPaymentMethodSelected(it)
        },
        isLoading = isLoading,
        loadError = loadError,
        cardFields = cardFields,
        cardValidationMessages = cardValidationMessages,
        currentLanguageTag = currentLanguageTag,
        onLanguageSelected = onLanguageSelected,
        submitErrorToastMessage = submitErrorToastMessage,
        onDismissSubmitError = { submitErrorToastMessage = null },
        onInfoClick = { showProtectionSheet = true },
        onBackgroundTap = { focusManager.clearFocus() },
        isSubmitting = isSubmitting,
        payButtonText =
            checkoutResult?.let {
                "${stringResource(Res.string.checkout_button_pay)} $${Amount(it.amount).apiFormatToFixed(2)}"
            } ?: stringResource(Res.string.checkout_button_pay),
        onPayClick = {
            val methodKey = selectedPaymentMethod ?: return@CheckoutMainContent
            val input =
                CheckoutPaymentMethodValidationInput(
                    cardFields = cardFields,
                    cardValidationMessages = cardValidationMessages,
                )
            val validator = CheckoutPaymentMethodValidators.forMethod(methodKey)
            if (!validator.validateBeforeSubmit(input)) return@CheckoutMainContent
            isSubmitting = true
            submitOrderScope.launch {
                submitOrder(
                    methodKey = methodKey,
                    tokenizerService = tokenizerService,
                    checkoutService = checkoutService,
                    cardInput =
                        CardPaymentInput(
                            cardNumber = cardFields.cardNumber.text,
                            expiryDate = cardFields.expiryDate.text,
                            cvv = cardFields.cvv.text,
                            cardholderName = cardFields.cardholderName.text,
                        ),
                    onOrderCreated = { result ->
                        orderResult = result
                        onOrderCreated?.invoke(result)
                    },
                    onError = onError,
                    onSubmitError = { submitErrorToastMessage = it },
                )
                isSubmitting = false
            }
        },
    )

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
private fun CheckoutMainContent(
    checkoutResult: CheckoutResult?,
    selectedPaymentMethod: String?,
    onMethodSelected: (String) -> Unit,
    isLoading: Boolean,
    loadError: String?,
    cardFields: CardFieldsState,
    cardValidationMessages: ValidationMessages,
    currentLanguageTag: String,
    onLanguageSelected: (String) -> Unit,
    submitErrorToastMessage: String?,
    onDismissSubmitError: () -> Unit,
    onInfoClick: () -> Unit,
    onBackgroundTap: () -> Unit,
    isSubmitting: Boolean,
    payButtonText: String,
    onPayClick: () -> Unit,
) {
    val isPayEnabled =
        selectedPaymentMethod?.let { methodKey ->
            CheckoutPaymentMethodValidators
                .forMethod(methodKey)
                .canSubmit(
                    CheckoutPaymentMethodValidationInput(
                        cardFields = cardFields,
                        cardValidationMessages = cardValidationMessages,
                    ),
                ) &&
                !isSubmitting
        } ?: false

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(colorFromHex(CDNResources.Colors.CHECKOUT_BACKGROUND))
                    .verticalScroll(rememberScrollState())
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { onBackgroundTap() })
                    },
        ) {
            CheckoutTotalRow(
                amountText = "$${Amount(checkoutResult?.amount ?: 0).apiFormatToFixed(2)}",
                lineItems = checkoutResult?.lineItems.orEmpty(),
                taxLines = checkoutResult?.taxLines.orEmpty(),
                discountLines = checkoutResult?.discountLines.orEmpty(),
                shippingLines = checkoutResult?.shippingLines.orEmpty(),
            )
            CheckoutHeader(onInfoClick = onInfoClick)

            CheckoutMethodSectionTitle(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 10.dp),
            )

            PaymentMethodsSection(
                methods = CheckoutMethodPolicy.filterSupportedMethods(checkoutResult?.allowedPaymentMethods.orEmpty()),
                selectedPaymentMethod = selectedPaymentMethod,
                onMethodSelected = onMethodSelected,
                loadingState = PaymentMethodsLoadingState(isLoading = isLoading, loadError = loadError),
                cardFields = cardFields,
                cashProviders = checkoutResult?.providers.orEmpty(),
                cardValidationMessages = cardValidationMessages,
            )

            ConektaButton(
                text = payButtonText,
                onClick = onPayClick,
                enabled = isPayEnabled,
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .testTag("checkout_pay_button"),
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
                onDismiss = onDismissSubmitError,
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }
    }
}

private suspend fun submitOrder(
    methodKey: String,
    tokenizerService: TokenizerApiService,
    checkoutService: CheckoutApiService,
    cardInput: CardPaymentInput,
    onOrderCreated: ((CheckoutOrderResult) -> Unit)?,
    onError: (CheckoutError) -> Unit,
    onSubmitError: (String) -> Unit,
) {
    try {
        val method =
            CheckoutPaymentMethod.from(
                methodKey = methodKey,
                tokenizerService = tokenizerService,
                cardNumber = cardInput.cardNumber,
                expiryDate = cardInput.expiryDate,
                cvv = cardInput.cvv,
                cardholderName = cardInput.cardholderName,
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
