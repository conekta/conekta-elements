package io.conekta.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.models.Amount
import io.conekta.elements.resources.CDNResources

private val CheckoutBg = colorFromHex(CDNResources.Colors.CHECKOUT_BACKGROUND)

@Composable
internal fun CheckoutSuccessShell(
    checkoutResult: CheckoutResult,
    merchantName: String,
    currentLanguageTag: String,
    onLanguageSelected: (String) -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    var showProtectionSheet by remember { mutableStateOf(false) }

    Box {
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
            Box(
                modifier = Modifier.fillMaxWidth(),
                content = content,
            )
            SuccessDetailCard(detail = checkoutResult.name)
            CheckoutFooter(
                selectedLanguageTag = currentLanguageTag,
                onLanguageSelected = onLanguageSelected,
            )
        }
    }

    if (showProtectionSheet) {
        PaymentProtectionSheet(
            merchantName = merchantName,
            onDismiss = { showProtectionSheet = false },
        )
    }
}
