package io.conekta.compose.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Material-style outlined info icon (circle with "i")
 * Uses EvenOdd fill to create the outlined appearance
 */
val InfoOutlinedIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "InfoOutlined",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.EvenOdd) {
            // "i" body
            moveTo(11f, 17f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-6f)
            horizontalLineToRelative(-2f)
            close()
            // Outer circle
            moveTo(12f, 2f)
            curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
            reflectiveCurveToRelative(4.48f, 10f, 10f, 10f)
            reflectiveCurveToRelative(10f, -4.48f, 10f, -10f)
            reflectiveCurveTo(17.52f, 2f, 12f, 2f)
            close()
            // Inner circle (cutout)
            moveTo(12f, 20f)
            curveToRelative(-4.41f, 0f, -8f, -3.59f, -8f, -8f)
            reflectiveCurveToRelative(3.59f, -8f, 8f, -8f)
            reflectiveCurveToRelative(8f, 3.59f, 8f, 8f)
            reflectiveCurveToRelative(-3.59f, 8f, -8f, 8f)
            close()
            // "i" dot
            moveTo(11f, 9f)
            horizontalLineToRelative(2f)
            verticalLineTo(7f)
            horizontalLineToRelative(-2f)
            close()
        }
    }.build()
}
