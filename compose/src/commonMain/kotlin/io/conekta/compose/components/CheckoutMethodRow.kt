package io.conekta.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.theme.LocalConektaFontFamily

@Composable
fun CheckoutMethodRow(
    methodLabel: String,
    iconUrl: String,
    selected: Boolean,
    onClick: () -> Unit,
    verticalPadding: Dp = 14.dp,
) {
    val fontFamily = LocalConektaFontFamily.current
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        RadioButton(selected = selected, onClick = onClick)
        AsyncImage(
            model = iconUrl,
            contentDescription = methodLabel,
            modifier = Modifier.size(18.dp),
            contentScale = ContentScale.Fit,
        )
        Text(
            text = methodLabel,
            style = TextStyle(fontFamily = fontFamily, fontSize = 16.sp, color = ConektaColors.Neutral8),
        )
    }
}
