package io.conekta.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.conekta.compose.components.banktransfer.CheckoutBankTransferMethodItem
import io.conekta.compose.components.banktransfer.CheckoutBankTransferMethodSection
import io.conekta.compose.components.card.CardFieldsState
import io.conekta.compose.components.card.CheckoutCardMethodItem
import io.conekta.compose.components.card.CheckoutCardMethodSection
import io.conekta.compose.components.cash.CheckoutCashMethodItem
import io.conekta.compose.components.cash.CheckoutCashMethodSection
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_error_loading
import io.conekta.compose.generated.resources.checkout_loading
import io.conekta.compose.generated.resources.placeholder_cardholder_name_checkout
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.utils.colorFromHex
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.checkout.models.CheckoutProvider
import io.conekta.elements.resources.CDNResources
import io.conekta.elements.tokenizer.validators.ValidationMessages
import io.conekta.elements.tokenizer.validators.validateForm
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PaymentMethodsSection(
    methods: List<String>,
    selectedPaymentMethod: String?,
    onMethodSelected: (String) -> Unit,
    loadingState: PaymentMethodsLoadingState,
    cardFields: CardFieldsState,
    cashProviders: List<CheckoutProvider>,
    cardValidationMessages: ValidationMessages,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
    ) {
        if (loadingState.isLoading) {
            PaymentMethodsLoadingContent()
            return@Surface
        }

        val loadError = loadingState.loadError
        if (loadError != null) {
            PaymentMethodsErrorContent(loadError)
            return@Surface
        }

        PaymentMethodsList(
            methods = methods,
            selectedPaymentMethod = selectedPaymentMethod,
            onMethodSelected = onMethodSelected,
            cardFields = cardFields,
            cashProviders = cashProviders,
            cardValidationMessages = cardValidationMessages,
        )
    }
}

internal data class PaymentMethodsLoadingState(
    val isLoading: Boolean,
    val loadError: String?,
)

@Composable
private fun PaymentMethodsLoadingContent() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = ConektaColors.CoreIndigo)
        Text(stringResource(Res.string.checkout_loading))
    }
}

@Composable
private fun PaymentMethodsErrorContent(loadError: String) {
    Text(
        text = loadError.ifBlank { stringResource(Res.string.checkout_error_loading) },
        color = ConektaColors.Error,
        modifier = Modifier.padding(16.dp),
    )
}

@Composable
private fun PaymentMethodsList(
    methods: List<String>,
    selectedPaymentMethod: String?,
    onMethodSelected: (String) -> Unit,
    cardFields: CardFieldsState,
    cashProviders: List<CheckoutProvider>,
    cardValidationMessages: ValidationMessages,
) {
    val selectedIndex = methods.indexOf(selectedPaymentMethod)
    Column {
        methods.forEachIndexed { index, method ->
            PaymentMethodRow(
                method = method,
                isSelected = selectedPaymentMethod == method,
                onMethodSelected = onMethodSelected,
                cardFields = cardFields,
                cashProviders = cashProviders,
                cardValidationMessages = cardValidationMessages,
            )

            if (shouldShowMethodDivider(index = index, lastIndex = methods.lastIndex, selectedIndex = selectedIndex)) {
                HorizontalDivider(color = colorFromHex(CDNResources.Colors.CHECKOUT_BORDER))
            }
        }
    }
}

@Composable
private fun PaymentMethodRow(
    method: String,
    isSelected: Boolean,
    onMethodSelected: (String) -> Unit,
    cardFields: CardFieldsState,
    cashProviders: List<CheckoutProvider>,
    cardValidationMessages: ValidationMessages,
) {
    when {
        method == CheckoutPaymentMethods.CARD && isSelected -> {
            SelectedCardMethodSection(
                method = method,
                onMethodSelected = onMethodSelected,
                cardFields = cardFields,
                cardValidationMessages = cardValidationMessages,
            )
        }
        method == CheckoutPaymentMethods.CARD -> {
            CheckoutCardMethodItem(selected = false, onClick = { onMethodSelected(method) })
        }
        method == CheckoutPaymentMethods.CASH && isSelected -> {
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
        method == CheckoutPaymentMethods.BANK_TRANSFER && isSelected -> {
            Column(modifier = Modifier.zIndex(1f)) {
                CheckoutBankTransferMethodSection(onSelect = { onMethodSelected(method) })
            }
        }
        method == CheckoutPaymentMethods.BANK_TRANSFER -> {
            CheckoutBankTransferMethodItem(selected = false, onClick = { onMethodSelected(method) })
        }
    }
}

@Composable
private fun SelectedCardMethodSection(
    method: String,
    onMethodSelected: (String) -> Unit,
    cardFields: CardFieldsState,
    cardValidationMessages: ValidationMessages,
) {
    Column(modifier = Modifier.zIndex(1f)) {
        CheckoutCardMethodSection(
            onSelect = { onMethodSelected(method) },
            cardholderName = cardFields.cardholderName,
            onCardholderNameChange = {
                cardFields.cardholderName = it
                cardFields.cardholderNameError = false
                cardFields.cardholderNameErrorMsg = null
            },
            cardNumber = cardFields.cardNumber,
            onCardNumberChange = {
                cardFields.cardNumber = it
                validateCardNumberField(cardFields = cardFields, value = it, messages = cardValidationMessages)
            },
            expiryDate = cardFields.expiryDate,
            onExpiryDateChange = {
                cardFields.expiryDate = it
                validateExpiryField(cardFields = cardFields, value = it, messages = cardValidationMessages)
            },
            cvv = cardFields.cvv,
            onCvvChange = {
                cardFields.cvv = it
                validateCvvField(cardFields = cardFields, value = it, messages = cardValidationMessages)
            },
            cardholderNamePlaceholderOverride = stringResource(Res.string.placeholder_cardholder_name_checkout),
            cardholderNameError = cardFields.cardholderNameError,
            cardholderNameErrorMessage = cardFields.cardholderNameErrorMsg,
            cardNumberError = cardFields.cardNumberError,
            cardNumberErrorMessage = cardFields.cardNumberErrorMsg,
            expiryDateError = cardFields.expiryDateError,
            expiryDateErrorMessage = cardFields.expiryDateErrorMsg,
            cvvError = cardFields.cvvError,
            cvvErrorMessage = cardFields.cvvErrorMsg,
        )
    }
}

private fun shouldShowMethodDivider(
    index: Int,
    lastIndex: Int,
    selectedIndex: Int,
): Boolean {
    if (index >= lastIndex) return false
    if (index == selectedIndex) return false
    if (index + 1 == selectedIndex) return false
    return true
}

private fun validateCardNumberField(
    cardFields: CardFieldsState,
    value: TextFieldValue,
    messages: ValidationMessages,
) {
    if (value.text.filter(Char::isDigit).isEmpty()) {
        cardFields.cardNumberError = false
        cardFields.cardNumberErrorMsg = null
        return
    }

    val validationResult =
        validateForm(
            cardholderName = cardFields.cardholderName.text,
            cardNumber = value.text,
            expiryDate = cardFields.expiryDate.text,
            cvv = cardFields.cvv.text,
            collectCardholderName = true,
            messages = messages,
        )
    cardFields.cardNumberError = validationResult.cardNumber.isError
    cardFields.cardNumberErrorMsg = validationResult.cardNumber.message
}

private fun validateExpiryField(
    cardFields: CardFieldsState,
    value: TextFieldValue,
    messages: ValidationMessages,
) {
    if (value.text.filter(Char::isDigit).isEmpty()) {
        cardFields.expiryDateError = false
        cardFields.expiryDateErrorMsg = null
        return
    }

    val validationResult =
        validateForm(
            cardholderName = cardFields.cardholderName.text,
            cardNumber = cardFields.cardNumber.text,
            expiryDate = value.text,
            cvv = cardFields.cvv.text,
            collectCardholderName = true,
            messages = messages,
        )
    cardFields.expiryDateError = validationResult.expiryDate.isError
    cardFields.expiryDateErrorMsg = validationResult.expiryDate.message
}

private fun validateCvvField(
    cardFields: CardFieldsState,
    value: TextFieldValue,
    messages: ValidationMessages,
) {
    if (value.text.filter(Char::isDigit).isEmpty()) {
        cardFields.cvvError = false
        cardFields.cvvErrorMsg = null
        return
    }

    val validationResult =
        validateForm(
            cardholderName = cardFields.cardholderName.text,
            cardNumber = cardFields.cardNumber.text,
            expiryDate = cardFields.expiryDate.text,
            cvv = value.text,
            collectCardholderName = true,
            messages = messages,
        )
    cardFields.cvvError = validationResult.cvv.isError
    cardFields.cvvErrorMsg = validationResult.cvv.message
}
