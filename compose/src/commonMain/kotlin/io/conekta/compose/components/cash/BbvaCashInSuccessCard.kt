package io.conekta.compose.components.cash

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.success_cash_bbva_step_1
import io.conekta.compose.generated.resources.success_cash_bbva_step_2
import io.conekta.compose.generated.resources.success_cash_bbva_step_3
import io.conekta.compose.generated.resources.success_cash_bbva_title
import io.conekta.compose.generated.resources.success_cash_commission_none
import io.conekta.compose.generated.resources.success_cash_convention_label
import io.conekta.compose.generated.resources.success_cash_reference_label
import io.conekta.compose.theme.LocalConektaFontFamily
import io.conekta.elements.checkout.models.CheckoutChargePaymentMethod
import io.conekta.elements.resources.CDNResources
import org.jetbrains.compose.resources.stringResource

private val BbvaCardBg = Color(0xFFFDFEFF)
private val BbvaCardBorder = Color(0xFFD8D8E8)
private val BbvaTitleColor = Color(0xFF585987)
private val BbvaTextPrimary = Color(0xFF212247)
private val BbvaTextSecondary = Color(0xFF8D8FBA)
private val BbvaTextBody = Color(0xFF585987)

@Composable
internal fun BbvaCashInSuccessCard(paymentMethod: CheckoutChargePaymentMethod) {
    val fontFamily = LocalConektaFontFamily.current

    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        color = BbvaCardBg,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BbvaCardBorder),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.success_cash_bbva_title),
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BbvaTitleColor,
                        letterSpacing = 0.7.sp,
                    ),
                textAlign = TextAlign.Center,
            )

            AsyncImage(
                model = CDNResources.Icons.BBVA,
                contentDescription = "BBVA",
                modifier = Modifier.width(142.dp).height(42.dp),
                contentScale = ContentScale.Fit,
            )

            Text(
                text = stringResource(Res.string.success_cash_commission_none),
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = BbvaTextPrimary,
                    ),
                textAlign = TextAlign.Center,
            )

            BbvaReferenceText(
                agreement = paymentMethod.agreement,
                reference = paymentMethod.reference,
            )

            BbvaCashInSteps(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun BbvaReferenceText(
    agreement: String,
    reference: String,
) {
    val fontFamily = LocalConektaFontFamily.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (agreement.isNotEmpty()) {
            val agreementText =
                buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontFamily = fontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BbvaTextSecondary,
                        ),
                    ) {
                        append(stringResource(Res.string.success_cash_convention_label))
                    }
                    append(" ")
                    withStyle(
                        SpanStyle(
                            fontFamily = fontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BbvaTextPrimary,
                        ),
                    ) {
                        append(agreement)
                    }
                }
            Text(text = agreementText, textAlign = TextAlign.Center)
        }

        val referenceText =
            buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontFamily = fontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BbvaTextSecondary,
                    ),
                ) {
                    append(stringResource(Res.string.success_cash_reference_label))
                }
                append(" ")
                withStyle(
                    SpanStyle(
                        fontFamily = fontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BbvaTextPrimary,
                    ),
                ) {
                    append(reference)
                }
            }
        Text(
            text = referenceText,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun BbvaCashInSteps(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BbvaStepRow(number = "1", text = stringResource(Res.string.success_cash_bbva_step_1))
        BbvaStepRow(number = "2", text = stringResource(Res.string.success_cash_bbva_step_2))
        BbvaStepRow(number = "3", text = stringResource(Res.string.success_cash_bbva_step_3))
    }
}

@Composable
private fun BbvaStepRow(
    number: String,
    text: String,
) {
    val fontFamily = LocalConektaFontFamily.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = "$number.",
            style =
                TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 22.sp,
                    color = BbvaTextBody,
                ),
            modifier = Modifier.width(16.dp),
            textAlign = TextAlign.Center,
        )
        Text(
            text = text,
            style =
                TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = BbvaTextBody,
                ),
            modifier = Modifier.weight(1f),
        )
    }
}
