package io.conekta.elements.checkout.models

import io.conekta.elements.resources.CDNResources

data class CheckoutCashProvidersUiData(
    val logoUrls: List<String>,
    val hasMoreProvidersLink: Boolean,
)

fun resolveCheckoutCashProvidersUiData(productTypes: List<String>): CheckoutCashProvidersUiData {
    val types = productTypes.map { it.trim() }.toSet()
    val hasBbvaCashIn = ProductTypes.BBVA_CASH_IN in types
    val hasCashIn = ProductTypes.CASH_IN in types

    return when {
        hasBbvaCashIn && hasCashIn ->
            CheckoutCashProvidersUiData(
                logoUrls =
                    listOf(
                        CDNResources.Icons.BBVA,
                        CDNResources.Icons.SEVEN_ELEVEN,
                        CDNResources.Icons.FARMACIA_DEL_AHORRO,
                        CDNResources.Icons.CIRCLE_K,
                        CDNResources.Icons.EXTRA,
                    ),
                hasMoreProvidersLink = true,
            )
        hasBbvaCashIn ->
            CheckoutCashProvidersUiData(
                logoUrls = listOf(CDNResources.Icons.BBVA),
                hasMoreProvidersLink = false,
            )
        hasCashIn ->
            CheckoutCashProvidersUiData(
                logoUrls =
                    listOf(
                        CDNResources.Icons.SEVEN_ELEVEN,
                        CDNResources.Icons.FARMACIA_DEL_AHORRO,
                        CDNResources.Icons.CIRCLE_K,
                        CDNResources.Icons.EXTRA,
                    ),
                hasMoreProvidersLink = true,
            )
        else ->
            CheckoutCashProvidersUiData(
                logoUrls = emptyList(),
                hasMoreProvidersLink = false,
            )
    }
}
