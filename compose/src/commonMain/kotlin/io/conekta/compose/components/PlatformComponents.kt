package io.conekta.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.conekta.elements.assets.CardBrandAssets
import io.conekta.elements.compose.generated.resources.*
import io.conekta.elements.tokenizer.models.CardBrand
import org.jetbrains.compose.resources.painterResource

/**
 * Shared UI Components using CDN-hosted assets
 *
 * ## Architecture Decision
 *
 * These components load assets from **Conekta's CDN** with local resources as fallback.
 *
 * ### Why CDN Assets?
 *
 * 1. **Always Up-to-Date**: Branding changes don't require app updates
 * 2. **Smaller Bundle Size**: Assets aren't embedded in the app
 * 3. **Consistency**: Same assets across web and mobile platforms
 * 4. **Easy Updates**: No app release needed for icon changes
 *
 * ### CDN URLs
 *
 * Assets are loaded from: `https://assets.conekta.com/cpanel/statics/assets/brands/logos/`
 * - `visa.svg` - Visa card logo
 * - `mastercard.svg` - Mastercard logo
 * - `amex.svg` - American Express logo
 * - `conekta-logo-24px.svg` - Conekta brand logo
 *
 * ### Fallback Strategy
 *
 * If CDN fails, components use local resources from `composeResources/drawable/`
 *
 * ### How It Works
 *
 * ```kotlin
 * // AsyncImage loads from CDN with local fallback
 * AsyncImage(
 *     model = CardBrandAssets.CardBrands.VISA,
 *     contentDescription = "Visa",
 *     error = painterResource(Res.drawable.ic_visa) // Fallback
 * )
 * ```
 *
 * ## References
 * - [Coil3 AsyncImage](https://coil-kt.github.io/coil/compose/)
 * - [Conekta CDN Assets](https://assets.conekta.com/cpanel/statics/assets/brands/logos/)
 *
 * @see io.conekta.elements.assets.CardBrandAssets
 * @see io.conekta.compose.components.ConektaLogoImage
 * @see io.conekta.compose.components.CardBrandIcon
 */

/**
 * Renders the Conekta logo from CDN with local fallback
 */
@Composable
fun ConektaLogoImage(modifier: Modifier = Modifier) {
    AsyncImage(
        model = CardBrandAssets.CONEKTA_LOGO,
        contentDescription = "Conekta Logo",
        modifier = modifier,
        error = painterResource(Res.drawable.ic_conekta_logo), // Fallback to local resource
        contentScale = ContentScale.Fit
    )
}

/**
 * Renders a card brand icon from CDN with local fallback
 */
@Composable
fun CardBrandIcon(
    brand: CardBrand,
    modifier: Modifier = Modifier
) {
    val cdnUrl = CardBrandAssets.getCardBrandUrl(brand) ?: return // Don't render unknown brands

    val fallbackResource = when (brand) {
        CardBrand.VISA -> Res.drawable.ic_visa
        CardBrand.MASTERCARD -> Res.drawable.ic_mastercard
        CardBrand.AMEX -> Res.drawable.ic_amex
        CardBrand.UNKNOWN -> return
    }

    AsyncImage(
        model = cdnUrl,
        contentDescription = "$brand card",
        modifier = modifier,
        error = painterResource(fallbackResource), // Fallback to local resource
        contentScale = ContentScale.Fit
    )
}

/**
 * Renders card brand icons in a row
 * Shows only the detected brand, or all brands if none detected
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
            // Show only detected brand
            CardBrandIcon(
                brand = detectedBrand,
                modifier = Modifier.size(24.dp)
            )
        } else {
            // Show all supported brands
            CardBrandIcon(brand = CardBrand.VISA, modifier = Modifier.size(24.dp))
            CardBrandIcon(brand = CardBrand.MASTERCARD, modifier = Modifier.size(24.dp))
            CardBrandIcon(brand = CardBrand.AMEX, modifier = Modifier.size(24.dp))
        }
    }
}

/**
 * Renders a check circle icon using Material Icons
 */
@Composable
fun CheckCircleIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = "Check",
        modifier = modifier
    )
}

