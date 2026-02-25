package io.conekta.compose.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.success_email_sent
import io.conekta.compose.theme.LocalConektaFontFamily
import org.jetbrains.compose.resources.stringResource

private val SuccessToastBg = Color(0xFFD8FDCF)
private val SuccessToastBorder = Color(0xFF9FE870)
private val SuccessToastText = Color(0xFF163300)
private val SuccessToastIcon = Color(0xFF51A81B)

@Composable
internal fun SuccessEmailToast(
    email: String,
    onDismiss: (() -> Unit)? = null,
) {
    val fontFamily = LocalConektaFontFamily.current

    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
        color = SuccessToastBg,
        shape =
            androidx.compose.foundation.shape
                .RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, SuccessToastBorder),
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = SuccessToastIcon,
                modifier = Modifier.size(32.dp),
            )

            Text(
                text = stringResource(Res.string.success_email_sent, email),
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = SuccessToastText,
                    ),
                modifier = Modifier.weight(1f),
            )

            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = null,
                tint = SuccessToastIcon,
                modifier =
                    Modifier
                        .size(24.dp)
                        .then(
                            if (onDismiss != null) {
                                Modifier.clickable(onClick = onDismiss)
                            } else {
                                Modifier
                            },
                        ),
            )
        }
    }
}
