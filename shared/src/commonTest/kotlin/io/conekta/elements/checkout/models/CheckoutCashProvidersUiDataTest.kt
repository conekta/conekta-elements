package io.conekta.elements.checkout.models

import io.conekta.elements.resources.CDNResources
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CheckoutCashProvidersUiDataTest {
    @Test
    fun `returns BBVA only when product type is bbva_cash_in`() {
        val result = resolveCheckoutCashProvidersUiData(listOf(ProductTypes.BBVA_CASH_IN))

        assertEquals(listOf(CDNResources.Icons.BBVA), result.logoUrls)
        assertFalse(result.hasMoreProvidersLink)
    }

    @Test
    fun `returns cash in logos and more link when product type is cash_in`() {
        val result = resolveCheckoutCashProvidersUiData(listOf(ProductTypes.CASH_IN))

        assertEquals(
            listOf(
                CDNResources.Icons.SEVEN_ELEVEN,
                CDNResources.Icons.FARMACIA_DEL_AHORRO,
                CDNResources.Icons.CIRCLE_K,
                CDNResources.Icons.EXTRA,
            ),
            result.logoUrls,
        )
        assertTrue(result.hasMoreProvidersLink)
    }

    @Test
    fun `returns mixed logos when both bbva and cash in are present`() {
        val result =
            resolveCheckoutCashProvidersUiData(
                listOf(
                    " ${ProductTypes.BBVA_CASH_IN} ",
                    ProductTypes.CASH_IN,
                ),
            )

        assertEquals(
            listOf(
                CDNResources.Icons.BBVA,
                CDNResources.Icons.SEVEN_ELEVEN,
                CDNResources.Icons.FARMACIA_DEL_AHORRO,
                CDNResources.Icons.CIRCLE_K,
                CDNResources.Icons.EXTRA,
            ),
            result.logoUrls,
        )
        assertTrue(result.hasMoreProvidersLink)
    }

    @Test
    fun `returns empty state when no supported product types are present`() {
        val result = resolveCheckoutCashProvidersUiData(listOf("unknown"))

        assertTrue(result.logoUrls.isEmpty())
        assertFalse(result.hasMoreProvidersLink)
    }
}
