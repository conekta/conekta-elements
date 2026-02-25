package io.conekta.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.success_card_purchase_completed
import io.conekta.compose.generated.resources.success_card_title
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.theme.LocalConektaFontFamily
import io.conekta.compose.utils.colorFromHex
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.models.Amount
import io.conekta.elements.resources.CDNResources
import org.jetbrains.compose.resources.stringResource

private val CardBorderColor = colorFromHex(CDNResources.Colors.CHECKOUT_BORDER)
private val SuccessBodyText = colorFromHex(CDNResources.Colors.SUCCESS_TEXT_SECONDARY)

@Composable
internal fun CardSuccessContent(
    checkoutResult: CheckoutResult,
    merchantName: String,
) {
    val amountText = "$${Amount(checkoutResult.amount.toInt()).toFixed(2)}"

    CardSuccessMessageCard(
        merchantName = merchantName,
        amountText = amountText,
    )
}

@Composable
private fun CardSuccessMessageCard(
    merchantName: String,
    amountText: String,
) {
    val fontFamily = LocalConektaFontFamily.current

    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        color = ConektaColors.Surface,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AsyncImage(
                model = CDNResources.Icons.SUCCESS_CHECK,
                contentDescription = null,
                modifier = Modifier.size(156.dp),
                contentScale = ContentScale.Fit,
            )

            Text(
                text = stringResource(Res.string.success_card_title),
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ConektaColors.DarkIndigo,
                    ),
            )

            Text(
                text = stringResource(Res.string.success_card_purchase_completed, merchantName, amountText),
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = SuccessBodyText,
                    ),
            )
        }
    }
}
