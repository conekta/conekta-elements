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
    const val BASE_CDN_URL = "https://assets.conekta.com"

    object Icons {
        private const val ICONS_PATH = "$BASE_CDN_URL/checkout/img/logos"
        private const val PAYMENT_METHOD_ICONS_PATH = "$BASE_CDN_URL/checkout/img/icons"
        const val APPLE = "$ICONS_PATH/logo-apple-with-text.svg"
        const val VISA = "$ICONS_PATH/logo-visa.svg"
        const val MASTERCARD = "$ICONS_PATH/logo-mastercard.svg"
        const val SPINNER = "$BASE_CDN_URL/checkout/img/icons/spinner.svg"
        const val CARD = "$PAYMENT_METHOD_ICONS_PATH/card.svg"
        const val BANK_TRANSFER = "$PAYMENT_METHOD_ICONS_PATH/bankTransfer.svg"
        const val CASH = "$PAYMENT_METHOD_ICONS_PATH/cash.svg"
        const val SUCCESS_CHECK = "$BASE_CDN_URL/checkout/img/Check.webp"
        const val BBVA = "$ICONS_PATH/small/bbva.svg"
        const val SEVEN_ELEVEN = "$ICONS_PATH/small/logo-seven-eleven.svg"
        const val FARMACIA_DEL_AHORRO = "$ICONS_PATH/small/logo-farmacia-del-ahorro.svg"
        const val CIRCLE_K = "$ICONS_PATH/small/logo-circlek.svg"
        const val EXTRA = "$ICONS_PATH/small/logo-extra.svg"
        const val WALMART = "$ICONS_PATH/logo-walmart.svg"
        const val BODEGA_AURRERA = "$ICONS_PATH/logo-bodega-aurrera.svg"
        const val SAMS_CLUB = "$ICONS_PATH/logo-sams-club.svg"
        const val KIOSKO = "$ICONS_PATH/logo-kiosko.svg"
    }

    object Colors {
        const val WHITE = "#fff"
        const val BLACK = "#000"
        const val DARK_BLUE = "#1E293B"
        const val CHECKOUT_BACKGROUND = "#F7F8FD"
        const val CHECKOUT_BORDER = "#D8D8E8"
        const val CHECKOUT_SELECTED_BORDER = "#212247"
        const val CHECKOUT_BREAKDOWN_BACKGROUND = "#050505"
        const val CHECKOUT_ON_SURFACE = "#020617"
        const val CHECKOUT_INK = "#0F112A"
        const val SUCCESS_SURFACE = "#FDFEFF"
        const val SUCCESS_TEXT_PRIMARY = "#212247"
        const val SUCCESS_TEXT_SECONDARY = "#585987"
        const val SUCCESS_TEXT_TERTIARY = "#8D8FBA"
        const val SUCCESS_ACCENT_BLUE = "#2C4CF5"
        const val SUCCESS_LINK_BLUE = "#090E94"
        const val SUCCESS_STEP_NUMBER_BG = "#E2E8F0"
        const val SUCCESS_COPY_BUTTON_BG = "#E5EDFF"
    }

    object Links {
        const val PRIVACY = "https://www.conekta.com/legal/privacy"
        const val HELP = "https://help.conekta.com/hc/es-419/sections/360007368253-Link-de-Pago"
        const val CASH_MAP = "https://map.conekta.com/"
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
