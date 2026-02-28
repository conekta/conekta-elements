package io.conekta.compose.components.cash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.theme.LocalConektaFontFamily
import io.conekta.compose.utils.colorFromHex
import io.conekta.elements.resources.CDNResources

@Composable
internal fun CashInStepRow(
    number: String,
    content: AnnotatedString,
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
                    .background(colorFromHex(CDNResources.Colors.SUCCESS_STEP_NUMBER_BG), RoundedCornerShape(12.dp)),
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
        Text(
            text = content,
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
