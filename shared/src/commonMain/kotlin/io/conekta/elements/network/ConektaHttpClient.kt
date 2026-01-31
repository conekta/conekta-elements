package io.conekta.elements.network

import io.conekta.elements.getPlatform
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal fun createHttpClient(config: ConektaConfig): HttpClient =
    HttpClient(httpClientEngine()) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                },
            )
        }

        install(Logging) {
            level = LogLevel.NONE
        }

        defaultRequest {
            url(config.baseUrl + "/")
            contentType(ContentType.Application.Json)
            headers.append("Accept", "application/vnd.conekta-v${config.apiVersion}+json")
            headers.append("Accept-Language", config.language)
            headers.append("Authorization", "Bearer ${config.apiKey}")
            headers.append("x-origin", platformOriginTag())
        }
    }

internal fun platformOriginTag(): String {
    val name = getPlatform().name.lowercase()
    return when {
        name.contains("android") -> "android"
        name.contains("ios") -> "ios"
        name.contains("web") -> "web"
        else -> "unknown"
    }
}
