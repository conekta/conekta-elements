package io.conekta.compose.components.cash

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_cash_provider_list_more
import io.conekta.compose.generated.resources.success_cash_commission_note
import io.conekta.compose.generated.resources.success_cash_step_1
import io.conekta.compose.generated.resources.success_cash_step_2_bold
import io.conekta.compose.generated.resources.success_cash_step_2_prefix
import io.conekta.compose.generated.resources.success_cash_step_2_suffix
import io.conekta.compose.generated.resources.success_cash_step_3
import io.conekta.compose.generated.resources.success_cash_store_title
import io.conekta.compose.generated.resources.success_see_map
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.theme.LocalConektaFontFamily
import io.conekta.elements.checkout.models.CheckoutChargePaymentMethod
import io.conekta.elements.checkout.models.ProductTypes
import io.conekta.elements.checkout.models.resolveCheckoutCashProvidersUiData
import io.conekta.elements.resources.CDNResources
import org.jetbrains.compose.resources.stringResource

private val CashInStepNumberBg = Color(0xFFE2E8F0)

@Composable
internal fun CashInSuccessCard(paymentMethod: CheckoutChargePaymentMethod) {
    val fontFamily = LocalConektaFontFamily.current
    val cashProvidersUiData =
        resolveCheckoutCashProvidersUiData(
            listOf(
                paymentMethod.productType.ifBlank { ProductTypes.CASH_IN },
            ),
        )
    val barcodeUrl = paymentMethod.barcodeUrl.trim()
    var showMoreProviders by remember(cashProvidersUiData.hasMoreProvidersLink) { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color(0xFFFDFEFF),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFD8D8E8)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.success_cash_store_title),
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF585987),
                        letterSpacing = 0.7.sp,
                    ),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (cashProvidersUiData.logoUrls.isNotEmpty()) {
                Row(
                    modifier =
                        Modifier
                            .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    cashProvidersUiData.logoUrls.forEach { url ->
                        CashProviderLogoImage(
                            url = url,
                            modifier = Modifier.height(32.dp),
                            contentScale = ContentScale.Fit,
                        )
                    }
                    if (cashProvidersUiData.hasMoreProvidersLink) {
                        CashProviderMoreLink(onClick = { showMoreProviders = !showMoreProviders })
                    }
                }
                if (cashProvidersUiData.hasMoreProvidersLink && showMoreProviders) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(Res.string.checkout_cash_provider_list_more),
                        style =
                            TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 14.sp,
                                color = Color(0xFF212247),
                            ),
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = stringResource(Res.string.success_cash_commission_note),
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        color = Color(0xFF212247),
                    ),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (barcodeUrl.isNotEmpty()) {
                AsyncImage(
                    model = barcodeUrl,
                    contentDescription = "Barcode for payment reference",
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    contentScale = ContentScale.FillWidth,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = paymentMethod.reference,
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212247),
                        letterSpacing = 0.36.sp,
                    ),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))
            CashInSteps()
        }
    }
}

@Composable
private fun CashInSteps() {
    val step2Text =
        buildAnnotatedString {
            append(stringResource(Res.string.success_cash_step_2_prefix))
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(stringResource(Res.string.success_cash_step_2_bold))
            }
            append(stringResource(Res.string.success_cash_step_2_suffix))
        }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        CashInStepRow(number = "1", text = stringResource(Res.string.success_cash_step_1))
        Row(modifier = Modifier.padding(start = 22.dp)) {
            CashInSeeMapLink()
        }
        CashInStepRow(number = "2", annotatedText = step2Text)
        CashInStepRow(number = "3", text = stringResource(Res.string.success_cash_step_3))
    }
}

@Composable
private fun CashInStepRow(
    number: String,
    text: String? = null,
    annotatedText: AnnotatedString? = null,
) {
    val fontFamily = LocalConektaFontFamily.current
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier =
                Modifier
                    .size(24.dp)
                    .background(CashInStepNumberBg, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number,
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ConektaColors.DarkIndigo,
                    ),
            )
        }
        if (annotatedText != null) {
            Text(
                text = annotatedText,
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        color = ConektaColors.DarkIndigo,
                    ),
                modifier = Modifier.weight(1f),
            )
        } else if (text != null) {
            Text(
                text = text,
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        color = ConektaColors.DarkIndigo,
                    ),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CashInSeeMapLink() {
    val fontFamily = LocalConektaFontFamily.current
    val uriHandler = LocalUriHandler.current
    Text(
        text = stringResource(Res.string.success_see_map),
        style =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = 14.sp,
                color = Color(0xFF090E94),
                textDecoration = TextDecoration.Underline,
            ),
        modifier =
            Modifier.clickable {
                uriHandler.openUri(CDNResources.Links.CASH_MAP)
            },
    )
}
