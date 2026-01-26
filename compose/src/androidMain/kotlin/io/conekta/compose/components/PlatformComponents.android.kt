package io.conekta.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.conekta.compose.models.CardBrand

/**
 * Android implementation of Conekta logo
 */
@Composable
actual fun ConektaLogoImage(modifier: Modifier) {
    val context = LocalContext.current
    val logoResId = context.resources.getIdentifier(
        "ic_conekta_logo",
        "drawable",
        context.packageName
    )
    
    if (logoResId != 0) {
        Image(
            painter = painterResource(id = logoResId),
            contentDescription = "Conekta Logo",
            modifier = modifier
        )
    }
}

/**
 * Android implementation of card brand icon
 */
@Composable
actual fun CardBrandIcon(
    brand: CardBrand,
    modifier: Modifier
) {
    val context = LocalContext.current
    val iconName = when (brand) {
        CardBrand.VISA -> "ic_visa_card"
        CardBrand.MASTERCARD -> "ic_mastercard_card"
        CardBrand.AMEX -> "ic_amex_card"
        else -> null
    }
    
    iconName?.let {
        val iconResId = context.resources.getIdentifier(
            it,
            "drawable",
            context.packageName
        )
        
        if (iconResId != 0) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = "$brand card",
                modifier = modifier
            )
        }
    }
}

/**
 * Android implementation of card brand icons row
 * Shows only the detected card brand icon
 */
@Composable
actual fun CardBrandIconsRow(
    detectedBrand: CardBrand?,
    modifier: Modifier
) {
    // Only show icon when a brand is detected and it's not UNKNOWN
    if (detectedBrand != null && detectedBrand != CardBrand.UNKNOWN) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CardBrandIcon(
                brand = detectedBrand,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Android implementation of check circle icon
 * Used in payment protection modal
 */
@Composable
actual fun CheckCircleIcon(modifier: Modifier) {
    val context = LocalContext.current
    val iconResId = context.resources.getIdentifier(
        "ic_check_circle",
        "drawable",
        context.packageName
    )
    
    if (iconResId != 0) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = "Check",
            modifier = modifier
        )
    }
}

