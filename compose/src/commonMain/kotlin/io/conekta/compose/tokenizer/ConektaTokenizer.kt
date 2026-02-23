package io.conekta.compose.tokenizer

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import io.conekta.compose.components.ConektaButton
import io.conekta.compose.components.ConektaCardFieldsSection
import io.conekta.compose.components.ConektaLogoImage
import io.conekta.compose.components.InfoOutlinedIcon
import io.conekta.compose.components.PaymentProtectionSheet
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.button_continue
import io.conekta.compose.generated.resources.button_processing
import io.conekta.compose.generated.resources.card_information_title
import io.conekta.compose.generated.resources.content_description_security_info
import io.conekta.compose.generated.resources.error_field_required
import io.conekta.compose.generated.resources.pay_securely_with
import io.conekta.compose.generated.resources.validation_card_min_length
import io.conekta.compose.generated.resources.validation_cvv_min_length
import io.conekta.compose.generated.resources.validation_expiry_year_invalid
import io.conekta.compose.generated.resources.validation_invalid_card
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.theme.ConektaTheme
import io.conekta.compose.theme.LocalConektaFontFamily
import io.conekta.elements.tokenizer.api.TokenizerApiException
import io.conekta.elements.tokenizer.api.TokenizerApiService
import io.conekta.elements.tokenizer.models.TokenResult
import io.conekta.elements.tokenizer.models.TokenizerConfig
import io.conekta.elements.tokenizer.models.TokenizerError
import io.conekta.elements.tokenizer.validators.ValidationMessages
import io.conekta.elements.tokenizer.validators.validateForm
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

/**
 * Conekta Tokenizer - Main public API
 *
 * A composable that renders a complete card tokenization form
 * following Conekta's design system.
 *
 * @param config Configuration for the tokenizer
 * @param onSuccess Callback when tokenization succeeds
 * @param onError Callback when tokenization fails
 * @param modifier Optional modifier for the tokenizer container
 *
 * Example usage:
 * ```
 * ConektaTokenizer(
 *     config = TokenizerConfig(
 *         publicKey = "key_xxx",
 *         merchantName = "My Store"
 *     ),
 *     onSuccess = { token -> /* handle token */ },
 *     onError = { error -> /* handle error */ }
 * )
 * ```
 */
@Composable
fun ConektaTokenizer(
    config: TokenizerConfig,
    onSuccess: (TokenResult) -> Unit,
    onError: (TokenizerError) -> Unit,
    modifier: Modifier = Modifier,
) {
    ConektaTheme {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = Color.White,
        ) {
            TokenizerContent(
                config = config,
                onSuccess = onSuccess,
                onError = onError,
            )
        }
    }
}

@Composable
private fun TokenizerContent(
    config: TokenizerConfig,
    onSuccess: (TokenResult) -> Unit,
    onError: (TokenizerError) -> Unit,
) {
    val fontFamily = LocalConektaFontFamily.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val tokenizerApiService = remember(config) { TokenizerApiService(config) }
    var cardholderName by remember { mutableStateOf(TextFieldValue("")) }
    var cardNumber by remember { mutableStateOf(TextFieldValue("")) }
    var expiryDate by remember { mutableStateOf(TextFieldValue("")) }
    var cvv by remember { mutableStateOf(TextFieldValue("")) }
    var showProtectionSheet by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    // Error states for each field
    var cardholderNameError by remember { mutableStateOf(false) }
    var cardNumberError by remember { mutableStateOf(false) }
    var expiryDateError by remember { mutableStateOf(false) }
    var cvvError by remember { mutableStateOf(false) }

    // Error messages for each field
    var cardholderNameErrorMsg by remember { mutableStateOf<String?>(null) }
    var cardNumberErrorMsg by remember { mutableStateOf<String?>(null) }
    var expiryDateErrorMsg by remember { mutableStateOf<String?>(null) }
    var cvvErrorMsg by remember { mutableStateOf<String?>(null) }

    // Strings
    val buttonContinue = stringResource(Res.string.button_continue)
    val buttonProcessing = stringResource(Res.string.button_processing)
    val validationMessages =
        ValidationMessages(
            required = stringResource(Res.string.error_field_required),
            cardMinLength = stringResource(Res.string.validation_card_min_length),
            invalidCard = stringResource(Res.string.validation_invalid_card),
            expiryYearInvalid = stringResource(Res.string.validation_expiry_year_invalid),
            cvvMinLength = stringResource(Res.string.validation_cvv_min_length),
        )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header
        TokenizerHeader(
            merchantName = config.merchantName,
            onInfoClick = { showProtectionSheet = true },
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
            text = stringResource(Res.string.card_information_title),
            style =
                TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ConektaColors.DarkIndigo,
                    lineHeight = 24.sp,
                ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        ConektaCardFieldsSection(
            collectCardholderName = config.collectCardholderName,
            cardholderName = cardholderName,
            onCardholderNameChange = {
                cardholderName = it
                cardholderNameError = false
                cardholderNameErrorMsg = null
            },
            cardNumber = cardNumber,
            onCardNumberChange = {
                cardNumber = it
                cardNumberError = false
                cardNumberErrorMsg = null
            },
            expiryDate = expiryDate,
            onExpiryDateChange = {
                expiryDate = it
                expiryDateError = false
                expiryDateErrorMsg = null
            },
            cvv = cvv,
            onCvvChange = {
                cvv = it
                cvvError = false
                cvvErrorMsg = null
            },
            enabled = !isProcessing,
            cardholderNameError = cardholderNameError,
            cardholderNameErrorMessage = cardholderNameErrorMsg,
            cardNumberError = cardNumberError,
            cardNumberErrorMessage = cardNumberErrorMsg,
            expiryDateError = expiryDateError,
            expiryDateErrorMessage = expiryDateErrorMsg,
            cvvError = cvvError,
            cvvErrorMessage = cvvErrorMsg,
        )

        Spacer(modifier = Modifier.weight(1f))

        // Submit Button
        ConektaButton(
            text = if (isProcessing) buttonProcessing else buttonContinue,
            onClick = {
                val result =
                    validateForm(
                        cardholderName = cardholderName.text,
                        cardNumber = cardNumber.text,
                        expiryDate = expiryDate.text,
                        cvv = cvv.text,
                        collectCardholderName = config.collectCardholderName,
                        messages = validationMessages,
                    )

                cardholderNameError = result.cardholderName.isError
                cardholderNameErrorMsg = result.cardholderName.message
                cardNumberError = result.cardNumber.isError
                cardNumberErrorMsg = result.cardNumber.message
                expiryDateError = result.expiryDate.isError
                expiryDateErrorMsg = result.expiryDate.message
                cvvError = result.cvv.isError
                cvvErrorMsg = result.cvv.message

                if (!result.hasError) {
                    isProcessing = true
                    val cardDigits = cardNumber.text.filter { it.isDigit() }
                    val expiryDigits = expiryDate.text.filter { it.isDigit() }
                    val expMonth = expiryDigits.take(2)
                    val expYear = expiryDigits.drop(2).take(2)
                    val cvvDigits = cvv.text.filter { it.isDigit() }

                    coroutineScope.launch {
                        val apiResult =
                            tokenizerApiService.tokenize(
                                cardNumber = cardDigits,
                                expMonth = expMonth,
                                expYear = expYear,
                                cvc = cvvDigits,
                                cardholderName = cardholderName.text,
                            )
                        apiResult
                            .onSuccess { tokenResult ->
                                onSuccess(tokenResult)
                            }.onFailure { error ->
                                onError((error as TokenizerApiException).tokenizerError)
                            }
                        isProcessing = false
                    }
                }
            },
            enabled = !isProcessing,
        )
    }

    // Payment Protection Sheet
    if (showProtectionSheet) {
        PaymentProtectionSheet(
            merchantName = config.merchantName,
            onDismiss = { showProtectionSheet = false },
        )
    }
}

@Composable
private fun TokenizerHeader(
    merchantName: String,
    onInfoClick: () -> Unit,
) {
    val fontFamily = LocalConektaFontFamily.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = stringResource(Res.string.pay_securely_with),
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ConektaColors.Neutral7,
                        letterSpacing = 0.7.sp,
                    ),
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Conekta Logo
            ConektaLogoImage(
                modifier =
                    Modifier
                        .width(120.dp)
                        .height(20.dp),
            )
        }

        IconButton(onClick = onInfoClick) {
            Icon(
                imageVector = InfoOutlinedIcon,
                contentDescription = stringResource(Res.string.content_description_security_info),
                tint = ConektaColors.Neutral7,
            )
        }
    }
}
