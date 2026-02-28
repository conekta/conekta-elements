package io.conekta.compose.components.cash

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.theme.LocalConektaFontFamily

@Composable
internal fun CashBarcodeReference(
    barcodeUrl: String,
    reference: String,
) {
    val fontFamily = LocalConektaFontFamily.current

    AsyncImage(
        model = barcodeUrl,
        contentDescription = "Barcode for payment reference",
        modifier = Modifier.fillMaxWidth().height(56.dp),
        contentScale = ContentScale.FillWidth,
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = reference,
        style =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ConektaColors.SuccessTextPrimary,
                letterSpacing = 0.36.sp,
            ),
        textAlign = TextAlign.Center,
    )
}
