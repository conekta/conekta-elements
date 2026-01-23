package io.conekta.elements.resources

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
object CDNResources {
    private const val CDN_BASE_URL = "https://assets.conekta.com"

    object Icons {
        private const val ICONS_PATH = "$CDN_BASE_URL/checkout/img/logos"
        const val APPLE = "$ICONS_PATH/logo-apple-with-text.svg"
        const val VISA = "$ICONS_PATH/logo-visa.svg"
        const val MASTERCARD = "$ICONS_PATH/logo-mastercard.svg"
    }
}
