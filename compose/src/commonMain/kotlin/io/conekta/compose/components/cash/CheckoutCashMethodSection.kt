package io.conekta.compose.components.cash

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import io.conekta.compose.components.CheckoutEmailReferenceRow
import io.conekta.compose.components.CheckoutMethodContent
import io.conekta.compose.components.CheckoutMethodRow
import io.conekta.compose.components.CheckoutSelectedMethodContainer
import io.conekta.compose.components.checkoutMethodBodyTextStyle
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_cash_points_message
import io.conekta.compose.generated.resources.checkout_cash_provider_list_more
import io.conekta.compose.generated.resources.checkout_cash_reference_message
import io.conekta.compose.generated.resources.checkout_cash_see_map
import io.conekta.compose.generated.resources.checkout_method_cash
import io.conekta.compose.theme.ConektaColors
import io.conekta.elements.checkout.models.CheckoutProvider
import io.conekta.elements.checkout.models.resolveCheckoutCashProvidersUiData
import io.conekta.elements.resources.CDNResources
import org.jetbrains.compose.resources.stringResource

internal fun shouldCenterSingleCashProviderLogo(
    logoCount: Int,
    showMoreLink: Boolean,
): Boolean = logoCount == 1 && !showMoreLink

@Composable
fun CheckoutCashMethodSection(
    onSelect: () -> Unit,
    providers: List<CheckoutProvider>,
) {
    val providerProductTypes = providers.map { it.productType }.filter { it.isNotBlank() }
    val cashProvidersUiData = resolveCheckoutCashProvidersUiData(providerProductTypes)
    var showMoreProviders by remember(cashProvidersUiData.hasMoreProvidersLink) { mutableStateOf(false) }

    CheckoutSelectedMethodContainer {
        CheckoutMethodRow(
            methodLabel = stringResource(Res.string.checkout_method_cash),
            iconUrl = CDNResources.Icons.CASH,
            selected = true,
            onClick = onSelect,
        )
        CheckoutMethodContent(topPadding = 4.dp) {
            CheckoutCashMapLinkText()
            Spacer(modifier = Modifier.height(6.dp))
            if (cashProvidersUiData.logoUrls.isNotEmpty()) {
                CheckoutCashProvidersRow(
                    logoUrls = cashProvidersUiData.logoUrls,
                    showMoreLink = cashProvidersUiData.hasMoreProvidersLink,
                    onMoreClick = { showMoreProviders = !showMoreProviders },
                )
            }
            if (cashProvidersUiData.hasMoreProvidersLink && showMoreProviders) {
                androidx.compose.material3.Text(
                    text = stringResource(Res.string.checkout_cash_provider_list_more),
                    style = checkoutMethodBodyTextStyle(),
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            CheckoutEmailReferenceRow(
                text = stringResource(Res.string.checkout_cash_reference_message),
            )
        }
    }
}

@Composable
private fun CheckoutCashMapLinkText() {
    val mapLabel = stringResource(Res.string.checkout_cash_see_map)
    val message =
        buildAnnotatedString {
            append("${stringResource(Res.string.checkout_cash_points_message)} ")
            withLink(
                LinkAnnotation.Url(
                    url = CDNResources.Links.CASH_MAP,
                    styles =
                        TextLinkStyles(
                            style =
                                SpanStyle(
                                    textDecoration = TextDecoration.Underline,
                                    color = ConektaColors.Neutral8,
                                ),
                        ),
                ),
            ) {
                append(mapLabel)
            }
        }

    androidx.compose.material3.Text(
        text = message,
        style = checkoutMethodBodyTextStyle(),
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

@Composable
private fun CheckoutCashProvidersRow(
    logoUrls: List<String>,
    showMoreLink: Boolean,
    onMoreClick: () -> Unit,
) {
    val shouldCenterSingleLogo =
        shouldCenterSingleCashProviderLogo(
            logoCount = logoUrls.size,
            showMoreLink = showMoreLink,
        )
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
        horizontalArrangement = if (shouldCenterSingleLogo) Arrangement.Center else Arrangement.spacedBy(10.dp),
    ) {
        logoUrls.forEach { url ->
            CashProviderLogoImage(
                url = url,
                modifier = Modifier.width(46.dp).height(20.dp),
                contentScale = ContentScale.Fit,
            )
        }
        if (showMoreLink) {
            CashProviderMoreLink(onClick = onMoreClick)
        }
    }
}
