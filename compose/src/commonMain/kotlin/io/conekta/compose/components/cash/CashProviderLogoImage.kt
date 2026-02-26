package io.conekta.compose.components.cash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
internal fun CashProviderLogoImage(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    var model by remember(url) { mutableStateOf(url) }

    AsyncImage(
        model = model,
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale,
        error = ColorPainter(Color(0xFFE2E8F0)),
        onError = { state ->
            println(
                "CashProviderLogoImage load failed: url=$model throwable=${state.result.throwable}",
            )
        },
    )
}
