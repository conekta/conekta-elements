package io.conekta.compose.components.card

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.conekta.compose.components.CheckoutMethodContentHorizontalPadding
import io.conekta.compose.components.CheckoutMethodRow
import io.conekta.compose.components.CheckoutSelectedMethodContainer
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_method_card
import io.conekta.elements.resources.CDNResources
import org.jetbrains.compose.resources.stringResource

@Composable
fun CheckoutCardMethodSection(
    onSelect: () -> Unit,
    cardholderName: TextFieldValue,
    onCardholderNameChange: (TextFieldValue) -> Unit,
    cardNumber: TextFieldValue,
    onCardNumberChange: (TextFieldValue) -> Unit,
    expiryDate: TextFieldValue,
    onExpiryDateChange: (TextFieldValue) -> Unit,
    cvv: TextFieldValue,
    onCvvChange: (TextFieldValue) -> Unit,
    cardholderNamePlaceholderOverride: String,
    cardholderNameError: Boolean = false,
    cardholderNameErrorMessage: String? = null,
    cardNumberError: Boolean = false,
    cardNumberErrorMessage: String? = null,
    expiryDateError: Boolean = false,
    expiryDateErrorMessage: String? = null,
    cvvError: Boolean = false,
    cvvErrorMessage: String? = null,
) {
    CheckoutSelectedMethodContainer {
        CheckoutMethodRow(
            methodLabel = stringResource(Res.string.checkout_method_card),
            iconUrl = CDNResources.Icons.CARD,
            selected = true,
            onClick = onSelect,
            verticalPadding = 10.dp,
        )
        ConektaCardFieldsSection(
            modifier =
                Modifier.padding(
                    start = CheckoutMethodContentHorizontalPadding,
                    end = CheckoutMethodContentHorizontalPadding,
                    bottom = 12.dp,
                ),
            cardholderName = cardholderName,
            onCardholderNameChange = onCardholderNameChange,
            cardNumber = cardNumber,
            onCardNumberChange = onCardNumberChange,
            expiryDate = expiryDate,
            onExpiryDateChange = onExpiryDateChange,
            cvv = cvv,
            onCvvChange = onCvvChange,
            cardholderNameFirst = false,
            cardholderNamePlaceholderOverride = cardholderNamePlaceholderOverride,
            cardholderNameError = cardholderNameError,
            cardholderNameErrorMessage = cardholderNameErrorMessage,
            cardNumberError = cardNumberError,
            cardNumberErrorMessage = cardNumberErrorMessage,
            expiryDateError = expiryDateError,
            expiryDateErrorMessage = expiryDateErrorMessage,
            cvvError = cvvError,
            cvvErrorMessage = cvvErrorMessage,
        )
    }
}

@Composable
fun CheckoutCardMethodItem(
    selected: Boolean,
    onClick: () -> Unit,
) {
    CheckoutMethodRow(
        methodLabel = stringResource(Res.string.checkout_method_card),
        iconUrl = CDNResources.Icons.CARD,
        selected = selected,
        onClick = onClick,
    )
}
