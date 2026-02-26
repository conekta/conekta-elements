package io.conekta.compose.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.conekta.compose.components.CardBrandIconsRow
import io.conekta.compose.components.ConektaTextField
import io.conekta.compose.components.CvvIcon
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.label_card_number
import io.conekta.compose.generated.resources.label_cardholder_name
import io.conekta.compose.generated.resources.label_cvv
import io.conekta.compose.generated.resources.label_expiry
import io.conekta.compose.generated.resources.placeholder_cardholder_name
import io.conekta.compose.generated.resources.placeholder_cvv
import io.conekta.compose.generated.resources.placeholder_expiry
import io.conekta.compose.tokenizer.CardFormatters
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConektaCardFieldsSection(
    cardholderName: TextFieldValue,
    onCardholderNameChange: (TextFieldValue) -> Unit,
    cardNumber: TextFieldValue,
    onCardNumberChange: (TextFieldValue) -> Unit,
    expiryDate: TextFieldValue,
    onExpiryDateChange: (TextFieldValue) -> Unit,
    cvv: TextFieldValue,
    onCvvChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    collectCardholderName: Boolean = true,
    enabled: Boolean = true,
    cardholderNameError: Boolean = false,
    cardholderNameErrorMessage: String? = null,
    cardNumberError: Boolean = false,
    cardNumberErrorMessage: String? = null,
    expiryDateError: Boolean = false,
    expiryDateErrorMessage: String? = null,
    cvvError: Boolean = false,
    cvvErrorMessage: String? = null,
    cardholderNameFirst: Boolean = true,
    cardholderNamePlaceholderOverride: String? = null,
) {
    val detectedBrand = remember(cardNumber.text) { CardFormatters.detectCardBrand(cardNumber.text) }
    val expiryFocusRequester = remember { FocusRequester() }
    val cvvFocusRequester = remember { FocusRequester() }

    val cardholderPlaceholder =
        cardholderNamePlaceholderOverride ?: stringResource(Res.string.placeholder_cardholder_name)

    @Composable
    fun CardholderField() {
        ConektaTextField(
            value = cardholderName,
            onValueChange = onCardholderNameChange,
            label = stringResource(Res.string.label_cardholder_name),
            placeholder = cardholderPlaceholder,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            enabled = enabled,
            isError = cardholderNameError,
            errorMessage = cardholderNameErrorMessage,
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (collectCardholderName && cardholderNameFirst) {
            CardholderField()
        }

        ConektaTextField(
            value = cardNumber,
            onValueChange = {
                val formatted = CardFormatters.formatCardNumber(it)
                onCardNumberChange(formatted)

                val previousDigitsCount = cardNumber.text.count(Char::isDigit)
                val currentDigitsCount = formatted.text.count(Char::isDigit)
                if (previousDigitsCount < 16 && currentDigitsCount == 16) {
                    expiryFocusRequester.requestFocus()
                }
            },
            label = stringResource(Res.string.label_card_number),
            placeholder = "0000 0000 0000 0000",
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next,
            enabled = enabled,
            isError = cardNumberError,
            errorMessage = cardNumberErrorMessage,
            trailingContent = {
                CardBrandIconsRow(detectedBrand = detectedBrand)
            },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ConektaTextField(
                value = expiryDate,
                onValueChange = {
                    val formatted = CardFormatters.formatExpiryDate(it)
                    onExpiryDateChange(formatted)

                    val previousDigitsCount = expiryDate.text.count(Char::isDigit)
                    val currentDigitsCount = formatted.text.count(Char::isDigit)
                    if (previousDigitsCount < 4 && currentDigitsCount == 4) {
                        cvvFocusRequester.requestFocus()
                    }
                },
                label = stringResource(Res.string.label_expiry),
                placeholder = stringResource(Res.string.placeholder_expiry),
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
                modifier = Modifier.weight(1f).focusRequester(expiryFocusRequester),
                enabled = enabled,
                isError = expiryDateError,
                errorMessage = expiryDateErrorMessage,
            )

            ConektaTextField(
                value = cvv,
                onValueChange = { onCvvChange(CardFormatters.formatCvv(it, detectedBrand)) },
                label = stringResource(Res.string.label_cvv),
                placeholder = stringResource(Res.string.placeholder_cvv),
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                modifier = Modifier.weight(1f).focusRequester(cvvFocusRequester),
                enabled = enabled,
                isError = cvvError,
                errorMessage = cvvErrorMessage,
                trailingContent = {
                    CvvIcon(modifier = Modifier.size(32.dp))
                },
            )
        }

        if (collectCardholderName && !cardholderNameFirst) {
            CardholderField()
        }
    }
}
