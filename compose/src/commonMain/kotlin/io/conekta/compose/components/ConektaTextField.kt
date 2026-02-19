package io.conekta.compose.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.theme.LocalConektaFontFamily

/**
 * Conekta styled text field component
 * Follows Figma design specifications
 */
@OptIn(ExperimentalMaterial3Api::class)
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
    errorMessage: String? = null,
) {
    val fontFamily = LocalConektaFontFamily.current
    val interactionSource = remember { MutableInteractionSource() }
    val textStyle =
        TextStyle(
            fontFamily = fontFamily,
            fontSize = 16.sp,
            color = ConektaColors.DarkIndigo,
        )
    val colors =
        OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) ConektaColors.Error else ConektaColors.Neutral5,
            unfocusedBorderColor = if (isError) ConektaColors.Error else ConektaColors.Neutral5,
            cursorColor = ConektaColors.DarkIndigo,
            focusedContainerColor = ConektaColors.Surface,
            unfocusedContainerColor = ConektaColors.Surface,
            disabledBorderColor = ConektaColors.Neutral5,
            disabledContainerColor = ConektaColors.Surface,
            errorBorderColor = ConektaColors.Error,
        )

    Column(modifier = modifier) {
        Text(
            text = label,
            style =
                TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = ConektaColors.Neutral8,
                    lineHeight = 22.sp,
                ),
        )

        Spacer(modifier = Modifier.height(4.dp))

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(43.dp),
            singleLine = true,
            textStyle = textStyle,
            cursorBrush = SolidColor(ConektaColors.DarkIndigo),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = imeAction,
                ),
            enabled = enabled,
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value.text,
                    innerTextField = innerTextField,
                    enabled = enabled,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    isError = isError,
                    placeholder = {
                        Text(
                            text = placeholder,
                            style =
                                TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 16.sp,
                                    color = ConektaColors.Neutral7,
                                ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(end = 4.dp),
                        )
                    },
                    trailingIcon = trailingContent,
                    colors = colors,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = enabled,
                            isError = isError,
                            interactionSource = interactionSource,
                            colors = colors,
                            shape = RoundedCornerShape(4.dp),
                        )
                    },
                )
            },
        )

        // Error message
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style =
                    TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = ConektaColors.Error,
                        lineHeight = 20.sp,
                    ),
            )
        }
    }
}
