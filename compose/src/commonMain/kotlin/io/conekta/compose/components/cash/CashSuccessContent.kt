package io.conekta.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.conekta.compose.utils.colorFromHex
import io.conekta.elements.checkout.models.CheckoutOrderResult
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.checkout.models.ProductTypes
import io.conekta.elements.models.Amount
import io.conekta.elements.resources.CDNResources

private val CheckoutBg = colorFromHex(CDNResources.Colors.CHECKOUT_BACKGROUND)

@Composable
internal fun CashSuccessContent(
    orderResult: CheckoutOrderResult,
    checkoutResult: CheckoutResult,
    merchantName: String,
    currentLanguageTag: String,
    onLanguageSelected: (String) -> Unit,
) {
    var showEmailToast by remember(checkoutResult.email) { mutableStateOf(checkoutResult.email.isNotEmpty()) }
    var showProtectionSheet by remember { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(CheckoutBg)
                .verticalScroll(rememberScrollState()),
    ) {
        CheckoutTotalRow(
            amountText = "$${Amount(checkoutResult.amount.toInt()).toFixed(2)}",
            lineItems = checkoutResult.lineItems,
            taxLines = checkoutResult.taxLines,
            discountLines = checkoutResult.discountLines,
            shippingLines = checkoutResult.shippingLines,
        )
        CheckoutHeader(onInfoClick = { showProtectionSheet = true })

        if (checkoutResult.email.isNotEmpty() && showEmailToast) {
            SuccessEmailToast(
                email = checkoutResult.email,
                onDismiss = { showEmailToast = false },
            )
        }

        CashSuccessPaySummaryCard(
            orderResult = orderResult,
            checkoutResult = checkoutResult,
        )

        orderResult.charges.forEach { charge ->
            val paymentMethod = charge.paymentMethod ?: return@forEach
            when {
                paymentMethod.productType.equals(ProductTypes.BBVA_CASH_IN, ignoreCase = true) ->
                    BbvaCashInSuccessCard(paymentMethod = paymentMethod)
                paymentMethod.productType.equals(ProductTypes.CASH_IN, ignoreCase = true) -> {
                    CashInSuccessCard(
                        paymentMethod = paymentMethod,
                    )
                }
            }
        }

        CheckoutFooter(
            selectedLanguageTag = currentLanguageTag,
            onLanguageSelected = onLanguageSelected,
        )
    }

    if (showProtectionSheet) {
        PaymentProtectionSheet(
            merchantName = merchantName,
            onDismiss = { showProtectionSheet = false },
        )
    }
}
