package io.conekta.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.success_bank_transfer_clabe_copied
import io.conekta.compose.utils.colorFromHex
import io.conekta.elements.checkout.models.CheckoutOrderResult
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.models.Amount
import io.conekta.elements.resources.CDNResources
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private val CheckoutBg = colorFromHex(CDNResources.Colors.CHECKOUT_BACKGROUND)

@Composable
internal fun BankTransferSuccessContent(
    orderResult: CheckoutOrderResult,
    checkoutResult: CheckoutResult,
    merchantName: String,
    currentLanguageTag: String,
    onLanguageSelected: (String) -> Unit,
) {
    var showEmailToast by remember(checkoutResult.email) { mutableStateOf(checkoutResult.email.isNotEmpty()) }
    var showProtectionSheet by remember { mutableStateOf(false) }
    var showCopiedToast by remember { mutableStateOf(false) }

    val paymentMethod = orderResult.charges.firstOrNull()?.paymentMethod
    val clabeReference =
        paymentMethod
            ?.clabe
            .orEmpty()
            .ifBlank { paymentMethod?.reference.orEmpty() }
    val expiresAt =
        orderResult.charges
            .firstOrNull()
            ?.paymentMethod
            ?.expiresAt ?: 0L
    val amountText = "$${Amount(checkoutResult.amount.toInt()).toFixed(2)}"

    LaunchedEffect(showCopiedToast) {
        if (showCopiedToast) {
            delay(1600)
            showCopiedToast = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

            BankTransferSuccessReferenceCard(
                amountText = amountText,
                clabeReference = clabeReference,
                expiresAt = expiresAt,
                onCopyClick = { showCopiedToast = true },
            )

            CheckoutFooter(
                selectedLanguageTag = currentLanguageTag,
                onLanguageSelected = onLanguageSelected,
            )
        }

        if (showCopiedToast) {
            CopyToast(
                text = stringResource(Res.string.success_bank_transfer_clabe_copied),
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
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
