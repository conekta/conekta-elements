package io.conekta.elements.network

data class ConektaConfig(
    val apiKey: String,
    val apiVersion: String = DEFAULT_API_VERSION,
    val language: String = DEFAULT_LANGUAGE,
    val baseUrl: String = DEFAULT_BASE_URL,
) {
    companion object {
        const val DEFAULT_BASE_URL = "https://api.conekta.io"
        const val DEFAULT_API_VERSION = "2.2.0"
        const val DEFAULT_LANGUAGE = "es"
    }
}
