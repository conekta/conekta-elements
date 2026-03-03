package io.conekta.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.success_expires_at
import io.conekta.compose.theme.LocalConektaFontFamily
import io.conekta.elements.utils.formatEpochSecondsInMexicoCity
import org.jetbrains.compose.resources.stringResource

private val PaySummaryTextColor = Color(0xFF212247)
private val PaySummaryIconColor = Color(0xFF2C4CF5)

@Composable
internal fun PaySummaryExpirationRow(epochSeconds: Long) {
    val fontFamily = LocalConektaFontFamily.current
    val (dateText, timeText) = formatEpochSecondsInMexicoCity(epochSeconds)

    val wrappedTimeText = timeText.replace(" ", "\u00A0")
    val expiresAtText = stringResource(Res.string.success_expires_at, dateText, wrappedTimeText)
    val highlightedText =
        buildAnnotatedString {
            append(expiresAtText)

            val dateStart = expiresAtText.indexOf(dateText)
            if (dateStart >= 0) {
                addStyle(
                    style = SpanStyle(fontWeight = FontWeight.Bold),
                    start = dateStart,
                    end = dateStart + dateText.length,
                )
            }

            val timeStart = expiresAtText.indexOf(wrappedTimeText)
            if (timeStart >= 0) {
                addStyle(
                    style = SpanStyle(fontWeight = FontWeight.Bold),
                    start = timeStart,
                    end = timeStart + wrappedTimeText.length,
                )
            }
        }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.AccessTime,
            contentDescription = null,
            tint = PaySummaryIconColor,
            modifier = Modifier.size(20.dp),
        )

        Text(
            text = highlightedText,
            style =
                TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 24.sp,
                    color = PaySummaryTextColor,
                ),
            modifier = Modifier.weight(1f),
        )
    }
}
