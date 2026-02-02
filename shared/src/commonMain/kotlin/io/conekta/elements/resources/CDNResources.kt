package io.conekta.elements.resources

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
object CDNResources {
    /**
     * Base CDN URL for all Conekta assets
     * This is the single source of truth for the CDN domain
     */
    const val baseCdnUrl = "https://assets.conekta.com"

    @Deprecated("Use baseCdnUrl instead", ReplaceWith("baseCdnUrl"))
    private const val CDN_BASE_URL = baseCdnUrl

    object Icons {
        private const val ICONS_PATH = "$baseCdnUrl/checkout/img/logos"
        const val APPLE = "$ICONS_PATH/logo-apple-with-text.svg"
        const val VISA = "$ICONS_PATH/logo-visa.svg"
        const val MASTERCARD = "$ICONS_PATH/logo-mastercard.svg"
        const val SPINNER = "$baseCdnUrl/checkout/img/icons/spinner.svg"
    }

    object Colors {
        const val WHITE = "#fff"
        const val BLACK = "#000"
        const val DARK_BLUE = "#1E293B"
    }

    object Opacity {
        const val DISABLED = 0.5
        const val ENABLED = 1.0
        const val HOVER = 0.8
    }

    object ApplePay {
        const val METHOD_IDENTIFIER = "https://apple.com/apple-pay"
        val MERCHANT_CAPABILITIES = arrayOf("supports3DS")
        val SUPPORTED_NETWORKS = arrayOf("amex", "masterCard", "visa")
        const val VERSION = 3
        const val COUNTRY_CODE_DEFAULT = "MX"
        const val MERCHANT_NAME_DEFAULT = "Conekta"
    }

    object ButtonSizes {
        const val MIN_BUTTON_WIDTH = 140
        const val MIN_BUTTON_HEIGHT = 30
    }
}
