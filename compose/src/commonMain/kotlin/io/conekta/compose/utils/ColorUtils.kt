package io.conekta.compose.utils

import androidx.compose.ui.graphics.Color

internal fun colorFromHex(hex: String): Color {
    val normalized =
        hex
            .trim()
            .removePrefix("#")
            .let {
                when (it.length) {
                    3 -> "${it[0]}${it[0]}${it[1]}${it[1]}${it[2]}${it[2]}"
                    6 -> it
                    8 -> it
                    else -> error("Unsupported color hex: $hex")
                }
            }

    val argb =
        when (normalized.length) {
            6 -> "FF$normalized"
            8 -> normalized
            else -> error("Unsupported color hex: $hex")
        }

    return Color(argb.toUInt(16).toInt())
}
