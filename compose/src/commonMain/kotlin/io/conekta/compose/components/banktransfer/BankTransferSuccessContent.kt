package io.conekta.compose.components.banktransfer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.conekta.compose.components.CopyToast
import io.conekta.compose.components.SuccessEmailToast
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.success_bank_transfer_clabe_copied
import io.conekta.elements.checkout.models.CheckoutOrderResult
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.models.Amount
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BankTransferSuccessContent(
    orderResult: CheckoutOrderResult,
    checkoutResult: CheckoutResult,
) {
    var showEmailToast by remember(checkoutResult.email) { mutableStateOf(checkoutResult.email.isNotEmpty()) }
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
    val amountText = "$${Amount(checkoutResult.amount).apiFormatToFixed(2)}"

    LaunchedEffect(showCopiedToast) {
        if (showCopiedToast) {
            delay(1600)
            showCopiedToast = false
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
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
}
