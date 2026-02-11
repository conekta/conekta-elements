package io.conekta.elements.assets

import io.conekta.elements.tokenizer.models.CardBrand
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CardBrandAssetsTest {
    @Test
    fun `CONEKTA_LOGO contains correct CDN base URL`() {
        assertTrue(CardBrandAssets.CONEKTA_LOGO.startsWith("https://assets.conekta.com"))
    }

    @Test
    fun `CONEKTA_LOGO points to SVG file`() {
        assertTrue(CardBrandAssets.CONEKTA_LOGO.endsWith(".svg"))
    }

    @Test
    fun `CONEKTA_LOGO contains conekta-logo-blue-full`() {
        assertTrue(CardBrandAssets.CONEKTA_LOGO.contains("conekta-logo-blue-full"))
    }

    // CardBrands URLs

    @Test
    fun `CardBrands VISA URL contains visa`() {
        assertTrue(CardBrandAssets.CardBrands.VISA.contains("visa"))
    }

    @Test
    fun `CardBrands MASTERCARD URL contains mastercard`() {
        assertTrue(CardBrandAssets.CardBrands.MASTERCARD.contains("mastercard"))
    }

    @Test
    fun `CardBrands AMEX URL contains amex`() {
        assertTrue(CardBrandAssets.CardBrands.AMEX.contains("amex"))
    }

    @Test
    fun `CardBrands ALL_CARDS URL is SVG`() {
        assertTrue(CardBrandAssets.CardBrands.ALL_CARDS.endsWith(".svg"))
    }

    @Test
    fun `all CardBrands URLs use HTTPS`() {
        assertTrue(CardBrandAssets.CardBrands.VISA.startsWith("https://"))
        assertTrue(CardBrandAssets.CardBrands.MASTERCARD.startsWith("https://"))
        assertTrue(CardBrandAssets.CardBrands.AMEX.startsWith("https://"))
        assertTrue(CardBrandAssets.CardBrands.ALL_CARDS.startsWith("https://"))
    }

    @Test
    fun `all CardBrands URLs are in brands logos path`() {
        assertTrue(CardBrandAssets.CardBrands.VISA.contains("/brands/logos/"))
        assertTrue(CardBrandAssets.CardBrands.MASTERCARD.contains("/brands/logos/"))
        assertTrue(CardBrandAssets.CardBrands.AMEX.contains("/brands/logos/"))
    }

    // Icons URLs

    @Test
    fun `Icons CLOSE URL contains close`() {
        assertTrue(CardBrandAssets.Icons.CLOSE.contains("close"))
    }

    @Test
    fun `Icons CHECK_CIRCLE URL contains check-circle`() {
        assertTrue(CardBrandAssets.Icons.CHECK_CIRCLE.contains("check-circle"))
    }

    @Test
    fun `Icons URLs use HTTPS`() {
        assertTrue(CardBrandAssets.Icons.CLOSE.startsWith("https://"))
        assertTrue(CardBrandAssets.Icons.CHECK_CIRCLE.startsWith("https://"))
    }

    @Test
    fun `Icons URLs are SVG files`() {
        assertTrue(CardBrandAssets.Icons.CLOSE.endsWith(".svg"))
        assertTrue(CardBrandAssets.Icons.CHECK_CIRCLE.endsWith(".svg"))
    }

    // getCardBrandUrl tests

    @Test
    fun `getCardBrandUrl returns VISA URL for VISA brand`() {
        val url = CardBrandAssets.getCardBrandUrl(CardBrand.VISA)
        assertNotNull(url)
        assertEquals(CardBrandAssets.CardBrands.VISA, url)
    }

    @Test
    fun `getCardBrandUrl returns MASTERCARD URL for MASTERCARD brand`() {
        val url = CardBrandAssets.getCardBrandUrl(CardBrand.MASTERCARD)
        assertNotNull(url)
        assertEquals(CardBrandAssets.CardBrands.MASTERCARD, url)
    }

    @Test
    fun `getCardBrandUrl returns AMEX URL for AMEX brand`() {
        val url = CardBrandAssets.getCardBrandUrl(CardBrand.AMEX)
        assertNotNull(url)
        assertEquals(CardBrandAssets.CardBrands.AMEX, url)
    }

    @Test
    fun `getCardBrandUrl returns null for UNKNOWN brand`() {
        val url = CardBrandAssets.getCardBrandUrl(CardBrand.UNKNOWN)
        assertNull(url)
    }

    @Test
    fun `getCardBrandUrl covers all CardBrand values`() {
        CardBrand.entries.forEach { brand ->
            val url = CardBrandAssets.getCardBrandUrl(brand)
            if (brand == CardBrand.UNKNOWN) {
                assertNull(url)
            } else {
                assertNotNull(url)
                assertTrue(url.startsWith("https://"))
            }
        }
    }
}
