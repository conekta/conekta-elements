package io.conekta.elements.resources

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CDNResourcesTest {
    @Test
    fun `BASE_CDN_URL is correct`() {
        assertEquals("https://assets.conekta.com", CDNResources.BASE_CDN_URL)
    }

    @Test
    fun `BASE_CDN_URL uses HTTPS`() {
        assertTrue(CDNResources.BASE_CDN_URL.startsWith("https://"))
    }

    // Icons

    @Test
    fun `Icons APPLE URL contains logo-apple`() {
        assertTrue(CDNResources.Icons.APPLE.contains("logo-apple"))
    }

    @Test
    fun `Icons VISA URL contains logo-visa`() {
        assertTrue(CDNResources.Icons.VISA.contains("logo-visa"))
    }

    @Test
    fun `Icons MASTERCARD URL contains logo-mastercard`() {
        assertTrue(CDNResources.Icons.MASTERCARD.contains("logo-mastercard"))
    }

    @Test
    fun `Icons SPINNER URL contains spinner`() {
        assertTrue(CDNResources.Icons.SPINNER.contains("spinner"))
    }

    @Test
    fun `all Icons URLs start with BASE_CDN_URL`() {
        assertTrue(CDNResources.Icons.APPLE.startsWith(CDNResources.BASE_CDN_URL))
        assertTrue(CDNResources.Icons.VISA.startsWith(CDNResources.BASE_CDN_URL))
        assertTrue(CDNResources.Icons.MASTERCARD.startsWith(CDNResources.BASE_CDN_URL))
        assertTrue(CDNResources.Icons.SPINNER.startsWith(CDNResources.BASE_CDN_URL))
    }

    @Test
    fun `all Icons URLs are SVG files`() {
        assertTrue(CDNResources.Icons.APPLE.endsWith(".svg"))
        assertTrue(CDNResources.Icons.VISA.endsWith(".svg"))
        assertTrue(CDNResources.Icons.MASTERCARD.endsWith(".svg"))
        assertTrue(CDNResources.Icons.SPINNER.endsWith(".svg"))
    }

    // Colors

    @Test
    fun `Colors WHITE is correct hex`() {
        assertEquals("#fff", CDNResources.Colors.WHITE)
    }

    @Test
    fun `Colors BLACK is correct hex`() {
        assertEquals("#000", CDNResources.Colors.BLACK)
    }

    @Test
    fun `Colors DARK_BLUE is correct hex`() {
        assertEquals("#1E293B", CDNResources.Colors.DARK_BLUE)
    }

    // Opacity

    @Test
    fun `Opacity DISABLED is 0 point 5`() {
        assertEquals(0.5, CDNResources.Opacity.DISABLED)
    }

    @Test
    fun `Opacity ENABLED is 1 point 0`() {
        assertEquals(1.0, CDNResources.Opacity.ENABLED)
    }

    @Test
    fun `Opacity HOVER is 0 point 8`() {
        assertEquals(0.8, CDNResources.Opacity.HOVER)
    }

    // ApplePay

    @Test
    fun `ApplePay METHOD_IDENTIFIER is correct URL`() {
        assertEquals("https://apple.com/apple-pay", CDNResources.ApplePay.METHOD_IDENTIFIER)
    }

    @Test
    fun `ApplePay MERCHANT_CAPABILITIES contains supports3DS`() {
        assertTrue(CDNResources.ApplePay.MERCHANT_CAPABILITIES.contains("supports3DS"))
    }

    @Test
    fun `ApplePay SUPPORTED_NETWORKS contains expected brands`() {
        val networks = CDNResources.ApplePay.SUPPORTED_NETWORKS
        assertTrue(networks.contains("amex"))
        assertTrue(networks.contains("masterCard"))
        assertTrue(networks.contains("visa"))
    }

    @Test
    fun `ApplePay VERSION is 3`() {
        assertEquals(3, CDNResources.ApplePay.VERSION)
    }

    @Test
    fun `ApplePay COUNTRY_CODE_DEFAULT is MX`() {
        assertEquals("MX", CDNResources.ApplePay.COUNTRY_CODE_DEFAULT)
    }

    @Test
    fun `ApplePay MERCHANT_NAME_DEFAULT is Conekta`() {
        assertEquals("Conekta", CDNResources.ApplePay.MERCHANT_NAME_DEFAULT)
    }

    // ButtonSizes

    @Test
    fun `ButtonSizes MIN_BUTTON_WIDTH is 140`() {
        assertEquals(140, CDNResources.ButtonSizes.MIN_BUTTON_WIDTH)
    }

    @Test
    fun `ButtonSizes MIN_BUTTON_HEIGHT is 30`() {
        assertEquals(30, CDNResources.ButtonSizes.MIN_BUTTON_HEIGHT)
    }
}
