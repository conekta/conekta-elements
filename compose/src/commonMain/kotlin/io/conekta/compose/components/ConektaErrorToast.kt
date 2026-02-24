package io.conekta.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_error_toast_title
import io.conekta.compose.theme.LocalConektaFontFamily
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ConektaErrorToast(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    autoDismissMillis: Long = 3500L,
) {
    LaunchedEffect(message) {
        delay(autoDismissMillis)
        onDismiss()
    }

    val titleColor = Color(0xFFFF9DAA)
    val bodyColor = Color(0xFFFFC5CD)
    val backgroundColor = Color(0xFF5A2B34)

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 6.dp,
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.ErrorOutline,
                        contentDescription = null,
                        tint = titleColor,
                    )
                    Text(
                        text = stringResource(Res.string.checkout_error_toast_title),
                        style =
                            TextStyle(
                                color = titleColor,
                                fontFamily = LocalConektaFontFamily.current,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                lineHeight = 20.sp,
                            ),
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = titleColor,
                    )
                }
            }
            Text(
                text = message,
                modifier = Modifier.padding(start = 32.dp),
                style =
                    TextStyle(
                        color = bodyColor,
                        fontFamily = LocalConektaFontFamily.current,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                    ),
            )
        }
    }
}
