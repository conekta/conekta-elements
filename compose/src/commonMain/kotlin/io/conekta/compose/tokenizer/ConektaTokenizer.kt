package io.conekta.compose.tokenizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.compose.components.CardBrandIconsRow
import io.conekta.compose.components.CheckCircleIcon
import io.conekta.compose.components.CloseIcon
import io.conekta.compose.components.ConektaButton
import io.conekta.compose.components.ConektaLogoImage
import io.conekta.compose.components.ConektaTextField
import io.conekta.compose.components.CvvIcon
import io.conekta.compose.components.InfoOutlinedIcon
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.theme.ConektaTheme
import io.conekta.compose.theme.LocalConektaFontFamily
import io.conekta.elements.compose.generated.resources.Res
import io.conekta.elements.compose.generated.resources.button_continue
import io.conekta.elements.compose.generated.resources.button_processing
import io.conekta.elements.compose.generated.resources.card_information_title
import io.conekta.elements.compose.generated.resources.conekta_description
import io.conekta.elements.compose.generated.resources.content_description_security_info
import io.conekta.elements.compose.generated.resources.error_field_required
import io.conekta.elements.compose.generated.resources.label_card_number
import io.conekta.elements.compose.generated.resources.label_cardholder_name
import io.conekta.elements.compose.generated.resources.label_cvv
import io.conekta.elements.compose.generated.resources.label_expiry
import io.conekta.elements.compose.generated.resources.pay_securely_with
import io.conekta.elements.compose.generated.resources.payment_protected
import io.conekta.elements.compose.generated.resources.placeholder_cardholder_name
import io.conekta.elements.compose.generated.resources.placeholder_cvv
import io.conekta.elements.compose.generated.resources.placeholder_expiry
import io.conekta.elements.compose.generated.resources.validation_card_min_length
import io.conekta.elements.compose.generated.resources.validation_cvv_min_length
import io.conekta.elements.compose.generated.resources.validation_expiry_year_invalid
import io.conekta.elements.tokenizer.models.TokenResult
import io.conekta.elements.tokenizer.models.TokenizerConfig
import io.conekta.elements.tokenizer.models.TokenizerError
import io.conekta.elements.tokenizer.validators.ValidationMessages
import io.conekta.elements.tokenizer.validators.validateForm
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
            expiryYearInvalid = stringResource(Res.string.validation_expiry_year_invalid),
            cvvMinLength = stringResource(Res.string.validation_cvv_min_length),
        )

    val detectedBrand =
        remember(cardNumber.text) {
            CardFormatters.detectCardBrand(cardNumber.text)
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ConektaColors.DarkIndigo,
                    lineHeight = 28.sp,
                ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Form
        if (config.collectCardholderName) {
            ConektaTextField(
                value = cardholderName,
                onValueChange = {
                    cardholderName = it
                    cardholderNameError = false
                    cardholderNameErrorMsg = null
                },
                label = stringResource(Res.string.label_cardholder_name),
                placeholder = stringResource(Res.string.placeholder_cardholder_name),
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                enabled = !isProcessing,
                isError = cardholderNameError,
                errorMessage = cardholderNameErrorMsg,
            )
        }

        // Card Number with brand icon
        ConektaTextField(
            value = cardNumber,
            onValueChange = { newValue ->
                cardNumber = CardFormatters.formatCardNumber(newValue)
                cardNumberError = false
                cardNumberErrorMsg = null
            },
            label = stringResource(Res.string.label_card_number),
            placeholder = "0000 0000 0000 0000",
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next,
            enabled = !isProcessing,
            isError = cardNumberError,
            errorMessage = cardNumberErrorMsg,
            trailingContent = {
                CardBrandIconsRow(
                    detectedBrand = detectedBrand,
                )
            },
        )

        // Expiry and CVV Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ConektaTextField(
                value = expiryDate,
                onValueChange = { newValue ->
                    expiryDate = CardFormatters.formatExpiryDate(newValue)
                    expiryDateError = false
                    expiryDateErrorMsg = null
                },
                label = stringResource(Res.string.label_expiry),
                placeholder = stringResource(Res.string.placeholder_expiry),
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
                modifier = Modifier.weight(1f),
                enabled = !isProcessing,
                isError = expiryDateError,
                errorMessage = expiryDateErrorMsg,
            )

            ConektaTextField(
                value = cvv,
                onValueChange = { newValue ->
                    cvv = CardFormatters.formatCvv(newValue, detectedBrand)
                    cvvError = false
                    cvvErrorMsg = null
                },
                label = stringResource(Res.string.label_cvv),
                placeholder = stringResource(Res.string.placeholder_cvv),
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                modifier = Modifier.weight(1f),
                enabled = !isProcessing,
                isError = cvvError,
                errorMessage = cvvErrorMsg,
                trailingContent = {
                    CvvIcon(
                        modifier = Modifier.size(32.dp),
                    )
                },
            )
        }

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
                        detectedBrand = detectedBrand,
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
                    onSuccess(
                        TokenResult(
                            token = "tok_test_mock_${cardDigits.takeLast(4)}",
                            cardBrand = detectedBrand.name,
                            lastFour = cardDigits.takeLast(4),
                        ),
                    )
                    isProcessing = false
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentProtectionSheet(
    merchantName: String,
    onDismiss: () -> Unit,
) {
    val fontFamily = LocalConektaFontFamily.current
    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = false,
        )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = ConektaColors.Neutral5,
            )
        },
        containerColor = ConektaColors.Surface,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        scrimColor = Color.Black.copy(alpha = 0.5f),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 32.dp),
        ) {
            // Close button (X) at top right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(onClick = onDismiss) {
                    CloseIcon(
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title with check icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Green check icon from Figma
                CheckCircleIcon(
                    modifier = Modifier.size(24.dp),
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(Res.string.payment_protected),
                    style =
                        TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ConektaColors.DarkIndigo,
                            lineHeight = 20.sp,
                        ),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description text
            Text(
                text = stringResource(Res.string.conekta_description, merchantName),
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = ConektaColors.Neutral8,
                        lineHeight = 22.sp,
                    ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
