package io.conekta.elements.assets

import io.conekta.elements.resources.CDNResources
import io.conekta.elements.tokenizer.models.CardBrand

/**
 * CDN URLs for card brand logos and Conekta assets
 *
 * These assets are hosted on Conekta's CDN to ensure:
 * - Always up-to-date branding
 * - Smaller app bundle size
 * - Consistency across web and mobile platforms
 * - Easy updates without app releases
 *
 * CDN Base URLs:
 * - Main assets: https://assets.conekta.com/cpanel/statics/assets
 * - Checkout assets: https://assets.conekta.com/checkout/img
 *
 * @see CDNResources for base CDN URL configuration
 * @see [Web implementation](https://github.com/conekta/int-payment-component/blob/main/src/app/util/constants.ts)
 */
object CardBrandAssets {
    private const val CPANEL_ASSETS_PATH = "/cpanel/statics/assets"
    private const val CHECKOUT_IMG_PATH = "/checkout/img"

    private const val AWS_S3_URL = "${CDNResources.BASE_CDN_URL}$CPANEL_ASSETS_PATH"
    private const val AWS_S3_URL_CHECKOUT = "${CDNResources.BASE_CDN_URL}$CHECKOUT_IMG_PATH"

    /**
     * Conekta logo (24px height optimized)
     */
    const val CONEKTA_LOGO = "$AWS_S3_URL/img/conekta-logo-blue-full.svg"

    /**
     * Card brand logos (optimized for card forms)
     */
    object CardBrands {
        const val VISA = "$AWS_S3_URL/brands/logos/visa.svg"
        const val MASTERCARD = "$AWS_S3_URL/brands/logos/mastercard.svg"
        const val AMEX = "$AWS_S3_URL/brands/logos/amex.svg"

        /**
         * All card brands combined logo
         */
        const val ALL_CARDS = "$AWS_S3_URL/brands/logos/MC%3AVisa%3AAMEX-rounded.svg"
    }

    /**
     * UI Icons (from Figma design system)
     */
    object Icons {
        /**
         * Close/X icon for dismissing dialogs
         */
        const val CLOSE = "$AWS_S3_URL/icons/close.svg"

        /**
         * Check circle icon for success states
         */
        const val CHECK_CIRCLE = "$AWS_S3_URL/icons/check-circle.svg"

        /**
         * CVV icon for security code field (32x32)
         */
        const val CVV = "$AWS_S3_URL/img/icons/cvv-icon-32x32.svg"
    }

    /**
     * Get CDN URL for a specific card brand
     * Returns null for UNKNOWN brands
     */
    fun getCardBrandUrl(brand: CardBrand): String? =
        when (brand) {
            CardBrand.VISA -> CardBrands.VISA
            CardBrand.MASTERCARD -> CardBrands.MASTERCARD
            CardBrand.AMEX -> CardBrands.AMEX
            CardBrand.UNKNOWN -> null
        }
}
