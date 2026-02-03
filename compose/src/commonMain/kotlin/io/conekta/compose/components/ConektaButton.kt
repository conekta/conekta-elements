package io.conekta.compose.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.compose.theme.ConektaColors

/**
 * Conekta styled button component
 * Follows Figma design specifications
 */
@Composable
fun ConektaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .height(48.dp),
        enabled = enabled,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = ConektaColors.CoreIndigo,
                contentColor = Color.White,
                disabledContainerColor = ConektaColors.Neutral5,
                disabledContentColor = ConektaColors.Neutral7,
            ),
        shape = RoundedCornerShape(4.dp),
    ) {
        Text(
            text = text,
            style =
                TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 24.sp,
                ),
        )
    }
}
