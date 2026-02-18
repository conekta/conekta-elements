package io.conekta.compose.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.conekta.elements.assets.CardBrandAssets
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.content_description_amex_card
import io.conekta.compose.generated.resources.content_description_card_brand
import io.conekta.compose.generated.resources.content_description_check
import io.conekta.compose.generated.resources.content_description_close
import io.conekta.compose.generated.resources.content_description_conekta_logo
import io.conekta.compose.generated.resources.content_description_cvv
import io.conekta.compose.generated.resources.content_description_mastercard_card
import io.conekta.compose.generated.resources.content_description_visa_card
import io.conekta.elements.tokenizer.models.CardBrand
import org.jetbrains.compose.resources.stringResource

/**
 * Shared UI Components using CDN-hosted assets
 *
 * ## Architecture Decision
 *
 * These components load assets exclusively from **Conekta's CDN**.
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
 * ### How It Works
 *
 * ```kotlin
 * // AsyncImage loads from CDN
 * AsyncImage(
 *     model = CardBrandAssets.CardBrands.VISA,
 *     contentDescription = "Visa"
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

@Composable
fun ConektaLogoImage(modifier: Modifier = Modifier) {
    AsyncImage(
        model = CardBrandAssets.CONEKTA_LOGO,
        contentDescription = stringResource(Res.string.content_description_conekta_logo),
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}

/**
 * Renders a card brand icon from CDN
 */
@Composable
fun CardBrandIcon(
    brand: CardBrand,
    modifier: Modifier = Modifier,
) {
    val cdnUrl = CardBrandAssets.getCardBrandUrl(brand) ?: return

    val contentDescription =
        when (brand) {
            CardBrand.VISA -> stringResource(Res.string.content_description_visa_card)
            CardBrand.MASTERCARD -> stringResource(Res.string.content_description_mastercard_card)
            CardBrand.AMEX -> stringResource(Res.string.content_description_amex_card)
            CardBrand.UNKNOWN -> stringResource(Res.string.content_description_card_brand)
        }

    val shape = RoundedCornerShape(4.dp)

    Box(
        modifier =
            modifier
                .clip(shape)
                .border(1.dp, Color(0xFFE0E0E0), shape),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        AsyncImage(
            model = cdnUrl,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

/**
 * Renders card brand icons in a row
 * Shows only the detected brand when recognized
 */
@Composable
fun CardBrandIconsRow(
    detectedBrand: CardBrand?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(end = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        val brandToShow = detectedBrand?.takeIf { !it.isUNKNOWN() }

        val iconModifier = Modifier.width(40.dp).height(28.dp)

        if (brandToShow != null) {
            CardBrandIcon(
                brand = brandToShow,
                modifier = iconModifier,
            )
        } else {
            CardBrandIcon(
                brand = CardBrand.VISA,
                modifier = iconModifier,
            )
            CardBrandIcon(
                brand = CardBrand.AMEX,
                modifier = iconModifier,
            )
            CardBrandIcon(
                brand = CardBrand.MASTERCARD,
                modifier = iconModifier,
            )
        }
    }
}

/**
 * Renders a close (X) icon using Material Icons
 */
@Composable
fun CloseIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = androidx.compose.material.icons.Icons.Default.Close,
        contentDescription = stringResource(Res.string.content_description_close),
        modifier = modifier,
        tint =
            androidx.compose.ui.graphics
                .Color(0xFF2A2A72),
    )
}

/**
 * Renders a check circle icon using Material Icons with Conekta green color
 */
@Composable
fun CheckCircleIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = stringResource(Res.string.content_description_check),
        modifier = modifier,
        tint =
            androidx.compose.ui.graphics
                .Color(0xFF10B981),
    )
}

/**
 * Renders CVV icon from CDN (32x32)
 */
@Composable
fun CvvIcon(modifier: Modifier = Modifier) {
    AsyncImage(
        model = CardBrandAssets.Icons.CVV,
        contentDescription = stringResource(Res.string.content_description_cvv),
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}
