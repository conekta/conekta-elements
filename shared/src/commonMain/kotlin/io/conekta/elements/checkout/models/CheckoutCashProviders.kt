package io.conekta.elements.checkout.models

import io.conekta.elements.resources.CDNResources

data class CheckoutCashProvidersUiData(
    val logoUrls: List<String>,
    val hasMoreProvidersLink: Boolean,
)

fun resolveCheckoutCashProvidersUiData(providers: List<CheckoutProvider>): CheckoutCashProvidersUiData {
    val normalizedProviderNames =
        providers
            .filter { it.paymentMethod == CheckoutPaymentMethods.CASH }
            .map { it.name.trim().lowercase() }
            .toSet()

    if (normalizedProviderNames.isEmpty()) {
        return CheckoutCashProvidersUiData(
            logoUrls = emptyList(),
            hasMoreProvidersLink = false,
        )
    }

    val logos = mutableListOf<String>()

    if ("bbva" in normalizedProviderNames) {
        logos += CDNResources.Icons.BBVA
    }

    val hasDatalogic = "datalogic" in normalizedProviderNames
    if (hasDatalogic) {
        logos +=
            listOf(
                CDNResources.Icons.SEVEN_ELEVEN,
                CDNResources.Icons.FARMACIA_DEL_AHORRO,
                CDNResources.Icons.CIRCLE_K,
                CDNResources.Icons.EXTRA,
            )
    }

    return CheckoutCashProvidersUiData(
        logoUrls = logos,
        hasMoreProvidersLink = hasDatalogic,
    )
}
