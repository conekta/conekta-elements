package io.conekta.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.content_description_security_info
import io.conekta.compose.generated.resources.pay_securely_with
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.theme.LocalConektaFontFamily
import org.jetbrains.compose.resources.stringResource

@Composable
fun CheckoutHeader(
    modifier: Modifier = Modifier,
    onInfoClick: () -> Unit = {},
) {
    val fontFamily = LocalConektaFontFamily.current
    Row(
        modifier = modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(Res.string.pay_securely_with),
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ConektaColors.Neutral8,
                        letterSpacing = 0.6.sp,
                    ),
            )
            ConektaLogoImage(
                modifier = Modifier.height(24.dp).fillMaxWidth(0.34f),
            )
        }

        IconButton(onClick = onInfoClick) {
            Icon(
                imageVector = InfoOutlinedIcon,
                contentDescription = stringResource(Res.string.content_description_security_info),
                tint = ConektaColors.Neutral7,
            )
        }
    }
}
