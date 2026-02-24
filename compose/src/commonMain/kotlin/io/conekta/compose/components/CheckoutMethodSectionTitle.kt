package io.conekta.compose.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_method_title
import io.conekta.compose.theme.LocalConektaFontFamily
import io.conekta.compose.utils.colorFromHex
import io.conekta.elements.resources.CDNResources
import org.jetbrains.compose.resources.stringResource

private val OnSurface = colorFromHex(CDNResources.Colors.CHECKOUT_ON_SURFACE)

@Composable
internal fun CheckoutMethodSectionTitle(modifier: Modifier = Modifier) {
    val fontFamily = LocalConektaFontFamily.current
    Text(
        text = stringResource(Res.string.checkout_method_title),
        style =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 12.sp,
                color = OnSurface,
            ),
        modifier = modifier,
    )
}
