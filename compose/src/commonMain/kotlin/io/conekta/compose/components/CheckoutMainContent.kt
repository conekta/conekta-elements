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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import io.conekta.compose.components.card.CardFieldsState
import io.conekta.compose.utils.colorFromHex
import io.conekta.elements.checkout.models.CheckoutMethodPolicy
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.models.Amount
import io.conekta.elements.resources.CDNResources
import io.conekta.elements.tokenizer.validators.ValidationMessages

@Composable
internal fun CheckoutMainContent(
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
