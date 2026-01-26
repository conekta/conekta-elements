package io.conekta.compose.tokenizer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.conekta.compose.models.CardBrand

/**
 * Android-specific implementations for Tokenizer components
 */

/**
 * Renders the Conekta logo using Android resources
 */
@Composable
fun ConektaLogoImage(modifier: Modifier = Modifier) {
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
            modifier = modifier.height(20.dp)
        )
    }
}

/**
 * Renders card brand icons using Android resources
 */
@Composable
fun CardBrandIcon(
    brand: CardBrand,
    modifier: Modifier = Modifier
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
                modifier = modifier.size(24.dp)
            )
        }
    }
}

/**
 * Renders all card brand icons in a row
 */
@Composable
fun CardBrandIconsRow(
    detectedBrand: CardBrand?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (detectedBrand != null && detectedBrand != CardBrand.UNKNOWN) {
            CardBrandIcon(brand = detectedBrand)
        } else {
            // Show all brands when no brand is detected
            CardBrandIcon(brand = CardBrand.VISA)
            CardBrandIcon(brand = CardBrand.MASTERCARD)
            CardBrandIcon(brand = CardBrand.AMEX)
        }
    }
}

