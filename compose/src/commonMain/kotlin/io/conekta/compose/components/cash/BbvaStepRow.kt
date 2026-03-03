package io.conekta.compose.components.cash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.theme.LocalConektaFontFamily

@Composable
internal fun BbvaStepRow(
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
                    color = ConektaColors.Neutral8,
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
                    color = ConektaColors.Neutral8,
                ),
            modifier = Modifier.weight(1f),
        )
    }
}
