package io.conekta.compose.components.cash

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.conekta.compose.components.SuccessEmailToast
import io.conekta.elements.checkout.models.CheckoutOrderResult
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.checkout.models.ProductTypes

@Composable
internal fun CashSuccessContent(
    orderResult: CheckoutOrderResult,
    checkoutResult: CheckoutResult,
) {
    var showEmailToast by remember(checkoutResult.email) { mutableStateOf(checkoutResult.email.isNotEmpty()) }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
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
    }
}
