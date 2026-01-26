package io.conekta.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.conekta.compose.models.CardBrand

/**
 * Platform-specific component for rendering the Conekta logo
 */
@Composable
expect fun ConektaLogoImage(modifier: Modifier = Modifier)

/**
 * Platform-specific component for rendering card brand icon
 */
@Composable
expect fun CardBrandIcon(
    brand: CardBrand,
    modifier: Modifier = Modifier
)

/**
 * Platform-specific component for rendering card brand icons row
 */
@Composable
expect fun CardBrandIconsRow(
    detectedBrand: CardBrand?,
    modifier: Modifier = Modifier
)

/**
 * Platform-specific component for rendering check circle icon
 * Used in payment protection modal
 */
@Composable
expect fun CheckCircleIcon(modifier: Modifier = Modifier)

