package io.conekta.compose.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_cash_points_message
import io.conekta.compose.generated.resources.checkout_cash_reference_message
import io.conekta.compose.generated.resources.checkout_cash_see_map
import io.conekta.compose.generated.resources.checkout_method_cash
import io.conekta.compose.theme.ConektaColors
import io.conekta.elements.resources.CDNResources
import org.jetbrains.compose.resources.stringResource

@Composable
fun CheckoutCashMethodSection(onSelect: () -> Unit) {
    CheckoutSelectedMethodContainer {
        CheckoutMethodRow(
            methodLabel = stringResource(Res.string.checkout_method_cash),
            iconUrl = CDNResources.Icons.CASH,
            selected = true,
            onClick = onSelect,
        )
        CheckoutMethodContent {
            CheckoutCashMapLinkText()
            CheckoutEmailReferenceRow(
                text = stringResource(Res.string.checkout_cash_reference_message),
            )
        }
    }
}

@Composable
private fun CheckoutCashMapLinkText() {
    val uriHandler = LocalUriHandler.current
    val mapLabel = stringResource(Res.string.checkout_cash_see_map)
    val message =
        buildAnnotatedString {
            append("${stringResource(Res.string.checkout_cash_points_message)} ")
            pushStringAnnotation(tag = "map_url", annotation = CDNResources.Links.CASH_MAP)
            withStyle(SpanStyle(textDecoration = TextDecoration.Underline, color = ConektaColors.Neutral8)) {
                append(mapLabel)
            }
            pop()
        }

    ClickableText(
        text = message,
        style = checkoutMethodBodyTextStyle(),
        onClick = { offset ->
            message
                .getStringAnnotations(tag = "map_url", start = offset, end = offset)
                .firstOrNull()
                ?.let { uriHandler.openUri(it.item) }
        },
    )
}

@Composable
fun CheckoutCashMethodItem(
    selected: Boolean,
    onClick: () -> Unit,
) {
    CheckoutMethodRow(
        methodLabel = stringResource(Res.string.checkout_method_cash),
        iconUrl = CDNResources.Icons.CASH,
        selected = selected,
        onClick = onClick,
    )
}
