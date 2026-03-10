package io.conekta.elements.network

data class CheckoutSsrConfig(
    val baseUrl: String = DEFAULT_BASE_URL,
    val language: String = DEFAULT_LANGUAGE,
    val source: String = DEFAULT_SOURCE,
) {
    companion object {
        const val DEFAULT_BASE_URL = "https://localhost:9092/"
        const val DEFAULT_LANGUAGE = "es"
        const val DEFAULT_SOURCE = "conekta-elements"
    }
}
