package io.conekta.compose.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MarkEmailUnread
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.theme.LocalConektaFontFamily
import io.conekta.compose.utils.colorFromHex
import io.conekta.elements.resources.CDNResources

internal val CheckoutMethodSelectedBorder = colorFromHex(CDNResources.Colors.CHECKOUT_SELECTED_BORDER)
internal val CheckoutMethodSelectedShape = RoundedCornerShape(14.dp)
internal val CheckoutMethodContentHorizontalPadding = 12.dp

@Composable
internal fun checkoutMethodBodyTextStyle(): TextStyle =
    TextStyle(
        fontFamily = LocalConektaFontFamily.current,
        fontSize = 15.sp,
        color = ConektaColors.Neutral8,
    )

@Composable
internal fun CheckoutSelectedMethodContainer(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier =
            Modifier
                .border(1.dp, CheckoutMethodSelectedBorder, CheckoutMethodSelectedShape)
                .padding(bottom = 12.dp),
        content = content,
    )
}

@Composable
internal fun CheckoutMethodContent(
    topPadding: Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = CheckoutMethodContentHorizontalPadding,
                    end = CheckoutMethodContentHorizontalPadding,
                    top = topPadding,
                ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        content = content,
    )
}

@Composable
internal fun CheckoutEmailReferenceRow(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.MarkEmailUnread,
            contentDescription = null,
            tint = ConektaColors.Neutral7,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = text,
            style = checkoutMethodBodyTextStyle(),
        )
    }
}
