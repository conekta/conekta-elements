package io.conekta.compose.components.cash

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.success_see_map
import io.conekta.compose.theme.LocalConektaFontFamily
import io.conekta.compose.utils.colorFromHex
import io.conekta.elements.resources.CDNResources
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CashInSeeMapLink() {
    val fontFamily = LocalConektaFontFamily.current
    val uriHandler = LocalUriHandler.current

    Text(
        text = stringResource(Res.string.success_see_map),
        style =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = 14.sp,
                color = colorFromHex(CDNResources.Colors.SUCCESS_LINK_BLUE),
                textDecoration = TextDecoration.Underline,
            ),
        modifier = Modifier.clickable { uriHandler.openUri(CDNResources.Links.CASH_MAP) },
    )
}
