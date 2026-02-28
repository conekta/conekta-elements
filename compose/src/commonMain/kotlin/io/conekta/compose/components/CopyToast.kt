package io.conekta.compose.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.theme.LocalConektaFontFamily
import io.conekta.compose.utils.colorFromHex
import io.conekta.elements.resources.CDNResources

@Composable
internal fun CopyToast(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = ConektaColors.SuccessTextPrimary,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style =
                TextStyle(
                    fontFamily = LocalConektaFontFamily.current,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorFromHex(CDNResources.Colors.WHITE),
                ),
        )
    }
}
