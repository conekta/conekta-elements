package io.conekta.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_discount_label
import io.conekta.compose.generated.resources.checkout_quantity_label
import io.conekta.compose.generated.resources.checkout_shipping_label
import io.conekta.compose.generated.resources.checkout_subtotal_label
import io.conekta.compose.generated.resources.checkout_tax_label
import io.conekta.compose.generated.resources.checkout_total_title
import io.conekta.compose.theme.LocalConektaFontFamily
import io.conekta.compose.utils.colorFromHex
import io.conekta.elements.checkout.models.CheckoutAmountLine
import io.conekta.elements.checkout.models.CheckoutLineItem
import io.conekta.elements.models.Amount
import io.conekta.elements.resources.CDNResources
import org.jetbrains.compose.resources.stringResource

private val Ink = colorFromHex(CDNResources.Colors.CHECKOUT_INK)
private val Border = colorFromHex(CDNResources.Colors.CHECKOUT_BORDER)
private val BreakdownBg = colorFromHex(CDNResources.Colors.CHECKOUT_BREAKDOWN_BACKGROUND)
private val OnSurface = colorFromHex(CDNResources.Colors.CHECKOUT_ON_SURFACE)

@Composable
fun CheckoutTotalRow(
    amountText: String,
    lineItems: List<CheckoutLineItem> = emptyList(),
    taxLines: List<CheckoutAmountLine> = emptyList(),
    discountLines: List<CheckoutAmountLine> = emptyList(),
    shippingLines: List<CheckoutAmountLine> = emptyList(),
    modifier: Modifier = Modifier,
) {
    val fontFamily = LocalConektaFontFamily.current
    val hasBreakdown =
        lineItems.isNotEmpty() || taxLines.isNotEmpty() || discountLines.isNotEmpty() || shippingLines.isNotEmpty()
    var expanded by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(Color.White)
                .clickable(enabled = hasBreakdown) { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.ShoppingCart,
            contentDescription = null,
            tint = Ink,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = stringResource(Res.string.checkout_total_title),
            style =
                TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = OnSurface,
                ),
            modifier = Modifier.weight(1f),
        )
        Text(
            text = amountText,
            style =
                TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 20.sp,
                    color = OnSurface,
                ),
        )
        Icon(
            imageVector = if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
            contentDescription = null,
            tint = Ink,
            modifier = Modifier.size(22.dp),
        )
    }
    HorizontalDivider(color = Border)
    if (expanded && hasBreakdown) {
        CheckoutBreakdown(
            lineItems = lineItems,
            taxLines = taxLines,
            discountLines = discountLines,
            shippingLines = shippingLines,
        )
    }
}

@Composable
private fun CheckoutBreakdown(
    lineItems: List<CheckoutLineItem>,
    taxLines: List<CheckoutAmountLine>,
    discountLines: List<CheckoutAmountLine>,
    shippingLines: List<CheckoutAmountLine>,
) {
    val fontFamily = LocalConektaFontFamily.current
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(BreakdownBg)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        lineItems.forEach { item ->
            Text(
                text = item.name,
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    ),
            )
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(Res.string.checkout_quantity_label),
                    style = TextStyle(fontFamily = fontFamily, color = Color.White, fontSize = 15.sp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = item.quantity.toString(),
                    style = TextStyle(fontFamily = fontFamily, color = Color.White, fontSize = 15.sp),
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatAmount(item.unitPrice * item.quantity),
                    style =
                        TextStyle(
                            fontFamily = fontFamily,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                )
            }
        }

        if (lineItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            BreakdownAmountRow(
                label = stringResource(Res.string.checkout_subtotal_label),
                amount = lineItems.sumOf { it.unitPrice * it.quantity },
            )
        }

        taxLines.forEach { line ->
            BreakdownAmountRow(
                label = line.description.ifBlank { stringResource(Res.string.checkout_tax_label) },
                amount = line.amount,
            )
        }

        shippingLines.forEach { line ->
            BreakdownAmountRow(
                label = line.description.ifBlank { stringResource(Res.string.checkout_shipping_label) },
                amount = line.amount,
            )
        }

        discountLines.forEach { line ->
            BreakdownDiscountAmountRow(
                label = line.description.ifBlank { stringResource(Res.string.checkout_discount_label) },
                amount = line.amount,
            )
        }
    }
}

@Composable
private fun BreakdownAmountRow(
    label: String,
    amount: Long,
) {
    BreakdownAmountRow(label = label, amountTextOverride = formatAmount(amount))
}

@Composable
private fun BreakdownDiscountAmountRow(
    label: String,
    amount: Long,
) {
    BreakdownAmountRow(
        label = label,
        amountTextOverride = "-${formatAmount(amount)}",
    )
}

@Composable
private fun BreakdownAmountRow(
    label: String,
    amountTextOverride: String,
) {
    val fontFamily = LocalConektaFontFamily.current
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style =
                TextStyle(
                    fontFamily = fontFamily,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                ),
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = amountTextOverride,
            style =
                TextStyle(
                    fontFamily = fontFamily,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
        )
    }
}

private fun formatAmount(amountInCents: Long): String {
    return "$${Amount(amountInCents.toInt()).toFixed(2)}"
}
