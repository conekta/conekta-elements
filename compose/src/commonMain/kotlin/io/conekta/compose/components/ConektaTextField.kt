package io.conekta.compose.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.elements.theme.ConektaColors

/**
 * Conekta styled text field component
 * Follows Figma design specifications
 */
@Composable
fun ConektaTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = ConektaColors.Neutral8,
                lineHeight = 22.sp
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = ConektaColors.Neutral7,
                        lineHeight = 24.sp
                    )
                )
            },
            trailingIcon = trailingContent,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = ConektaColors.DarkIndigo,
                lineHeight = 24.sp
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) ConektaColors.Error else ConektaColors.Neutral5,
                unfocusedBorderColor = if (isError) ConektaColors.Error else ConektaColors.Neutral5,
                cursorColor = ConektaColors.DarkIndigo,
                focusedContainerColor = ConektaColors.Surface,
                unfocusedContainerColor = ConektaColors.Surface,
                disabledBorderColor = ConektaColors.Neutral5,
                disabledContainerColor = ConektaColors.Surface,
                errorBorderColor = ConektaColors.Error
            ),
            shape = RoundedCornerShape(4.dp),
            enabled = enabled,
            isError = isError
        )
        
        // Error message
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = ConektaColors.Error,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

