package io.conekta.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.success_cash_must_pay
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.theme.LocalConektaFontFamily
import io.conekta.elements.checkout.models.CheckoutOrderResult
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.models.Amount
import org.jetbrains.compose.resources.stringResource

private val PaySummaryCardBg = Color(0xFFF7F8FD)
private val PaySummaryTextColor = Color(0xFF212247)

@Composable
internal fun CashSuccessPaySummaryCard(
    orderResult: CheckoutOrderResult,
    checkoutResult: CheckoutResult,
) {
    val fontFamily = LocalConektaFontFamily.current
    val expiresAt =
        orderResult.charges
            .firstOrNull()
            ?.paymentMethod
            ?.expiresAt ?: 0L

    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        color = PaySummaryCardBg,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 27.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.success_cash_must_pay),
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = PaySummaryTextColor,
                    ),
                textAlign = TextAlign.Center,
            )

            Text(
                text = "$${Amount(checkoutResult.amount.toInt()).toFixed(2)} ${checkoutResult.currency}",
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 44.sp,
                        letterSpacing = (-1).sp,
                        color = ConektaColors.DarkIndigo,
                    ),
                textAlign = TextAlign.Center,
            )

            if (expiresAt > 0) {
                Spacer(modifier = Modifier.height(2.dp))
                PaySummaryExpirationRow(epochSeconds = expiresAt)
            }
        }
    }
}
