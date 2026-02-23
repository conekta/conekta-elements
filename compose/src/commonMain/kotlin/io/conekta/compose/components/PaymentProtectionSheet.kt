package io.conekta.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.conekta_description
import io.conekta.compose.generated.resources.payment_protected
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.theme.LocalConektaFontFamily
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentProtectionSheet(
    merchantName: String,
    onDismiss: () -> Unit,
) {
    val fontFamily = LocalConektaFontFamily.current
    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = false,
        )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = ConektaColors.Neutral5,
            )
        },
        containerColor = ConektaColors.Surface,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        scrimColor = Color.Black.copy(alpha = 0.5f),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 32.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(onClick = onDismiss) {
                    CloseIcon(
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CheckCircleIcon(
                    modifier = Modifier.size(24.dp),
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(Res.string.payment_protected),
                    style =
                        TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ConektaColors.DarkIndigo,
                            lineHeight = 20.sp,
                        ),
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = stringResource(Res.string.conekta_description, merchantName),
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = ConektaColors.Neutral8,
                        lineHeight = 22.sp,
                    ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
