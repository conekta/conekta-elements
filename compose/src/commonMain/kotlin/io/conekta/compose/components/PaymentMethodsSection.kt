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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.conekta.compose.checkout.CardFieldsState
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

private val Border = colorFromHex(CDNResources.Colors.CHECKOUT_BORDER)

@Composable
internal fun PaymentMethodsSection(
    methods: List<String>,
    selectedPaymentMethod: String?,
    onMethodSelected: (String) -> Unit,
    isLoading: Boolean,
    loadError: String?,
    cardFields: CardFieldsState,
    cashProviders: List<CheckoutProvider>,
    cardValidationMessages: ValidationMessages,
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
                                        cardholderName = cardFields.cardholderName,
                                        onCardholderNameChange = {
                                            cardFields.cardholderName = it
                                            cardFields.cardholderNameError = false
                                            cardFields.cardholderNameErrorMsg = null
                                        },
                                        cardNumber = cardFields.cardNumber,
                                        onCardNumberChange = {
                                            cardFields.cardNumber = it
                                            val cardDigits = it.text.filter(Char::isDigit)
                                            if (cardDigits.isEmpty()) {
                                                cardFields.cardNumberError = false
                                                cardFields.cardNumberErrorMsg = null
                                            } else {
                                                val validationResult =
                                                    validateForm(
                                                        cardholderName = cardFields.cardholderName.text,
                                                        cardNumber = it.text,
                                                        expiryDate = cardFields.expiryDate.text,
                                                        cvv = cardFields.cvv.text,
                                                        collectCardholderName = true,
                                                        messages = cardValidationMessages,
                                                    )
                                                cardFields.cardNumberError = validationResult.cardNumber.isError
                                                cardFields.cardNumberErrorMsg = validationResult.cardNumber.message
                                            }
                                        },
                                        expiryDate = cardFields.expiryDate,
                                        onExpiryDateChange = {
                                            cardFields.expiryDate = it
                                            val expiryDigits = it.text.filter(Char::isDigit)
                                            if (expiryDigits.isEmpty()) {
                                                cardFields.expiryDateError = false
                                                cardFields.expiryDateErrorMsg = null
                                            } else {
                                                val validationResult =
                                                    validateForm(
                                                        cardholderName = cardFields.cardholderName.text,
                                                        cardNumber = cardFields.cardNumber.text,
                                                        expiryDate = it.text,
                                                        cvv = cardFields.cvv.text,
                                                        collectCardholderName = true,
                                                        messages = cardValidationMessages,
                                                    )
                                                cardFields.expiryDateError = validationResult.expiryDate.isError
                                                cardFields.expiryDateErrorMsg = validationResult.expiryDate.message
                                            }
                                        },
                                        cvv = cardFields.cvv,
                                        onCvvChange = {
                                            cardFields.cvv = it
                                            val cvvDigits = it.text.filter(Char::isDigit)
                                            if (cvvDigits.isEmpty()) {
                                                cardFields.cvvError = false
                                                cardFields.cvvErrorMsg = null
                                            } else {
                                                val validationResult =
                                                    validateForm(
                                                        cardholderName = cardFields.cardholderName.text,
                                                        cardNumber = cardFields.cardNumber.text,
                                                        expiryDate = cardFields.expiryDate.text,
                                                        cvv = it.text,
                                                        collectCardholderName = true,
                                                        messages = cardValidationMessages,
                                                    )
                                                cardFields.cvvError = validationResult.cvv.isError
                                                cardFields.cvvErrorMsg = validationResult.cvv.message
                                            }
                                        },
                                        cardholderNamePlaceholderOverride =
                                            stringResource(Res.string.placeholder_cardholder_name_checkout),
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
