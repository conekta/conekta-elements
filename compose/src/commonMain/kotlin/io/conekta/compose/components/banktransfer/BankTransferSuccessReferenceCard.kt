package io.conekta.compose.components.banktransfer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.compose.components.PaySummaryExpirationRow
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.success_bank_transfer_clabe_format
import io.conekta.compose.generated.resources.success_bank_transfer_copy_number
import io.conekta.compose.generated.resources.success_bank_transfer_reference_title
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.theme.LocalConektaFontFamily
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BankTransferSuccessReferenceCard(
    amountText: String,
    clabeReference: String,
    expiresAt: Long,
    onCopyClick: () -> Unit,
) {
    val fontFamily = LocalConektaFontFamily.current

    @Suppress("DEPRECATION")
    val clipboardManager = LocalClipboardManager.current
    val hapticFeedback = LocalHapticFeedback.current

    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        color = ConektaColors.Surface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, ConektaColors.Neutral5),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.success_bank_transfer_reference_title),
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.7.sp,
                        color = ConektaColors.Neutral7,
                    ),
                textAlign = TextAlign.Center,
            )

            Text(
                text = amountText,
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 32.sp,
                        lineHeight = 40.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.64).sp,
                        color = ConektaColors.SuccessTextPrimary,
                    ),
                textAlign = TextAlign.Center,
            )

            if (expiresAt > 0) {
                PaySummaryExpirationRow(epochSeconds = expiresAt)
            }

            Text(
                text = stringResource(Res.string.success_bank_transfer_clabe_format, clabeReference),
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ConektaColors.SuccessTextPrimary,
                    ),
                textAlign = TextAlign.Center,
            )

            Surface(
                modifier =
                    Modifier
                        .sizeIn(minHeight = 32.dp)
                        .clickable {
                            if (clabeReference.isNotBlank()) {
                                clipboardManager.setText(AnnotatedString(clabeReference))
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                onCopyClick()
                            }
                        },
                color = ConektaColors.SuccessCopyButtonBackground,
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = stringResource(Res.string.success_bank_transfer_copy_number),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style =
                        TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 14.sp,
                            lineHeight = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = ConektaColors.SuccessAccentBlue,
                        ),
                )
            }
        }
    }
}
