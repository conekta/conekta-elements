package io.conekta.compose.components.cash

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_cash_provider_more_link
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.theme.LocalConektaFontFamily
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CashProviderMoreLink(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fontFamily = LocalConektaFontFamily.current
    Text(
        text = stringResource(Res.string.checkout_cash_provider_more_link),
        style =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = 14.sp,
                color = ConektaColors.SuccessTextPrimary,
                textDecoration = TextDecoration.Underline,
            ),
        modifier = modifier.clickable(onClick = onClick),
    )
}
