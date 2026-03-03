package io.conekta.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_error_toast_title
import io.conekta.compose.theme.ConektaColors
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

    val titleColor = ConektaColors.ErrorToastTitle
    val bodyColor = ConektaColors.ErrorToastBody
    val backgroundColor = ConektaColors.ErrorToastBackground

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 6.dp,
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
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
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = titleColor,
                    modifier =
                        Modifier
                            .padding(top = 2.dp, end = 2.dp)
                            .size(18.dp)
                            .clickable(onClick = onDismiss),
                )
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
