package io.conekta.elements.checkout.api

import io.conekta.elements.checkout.models.CheckoutError
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject

internal class CheckoutApiErrorMapper(
    private val json: Json,
) {
    fun mapExceptionToNetworkError(
        throwable: Throwable,
        defaultMessage: String = "Unknown network error",
    ): CheckoutError.NetworkError = CheckoutError.NetworkError(throwable.asNetworkErrorMessage(defaultMessage))

    fun buildHttpErrorResult(
        statusCode: Int,
        errorBody: String,
    ): Result<Nothing> {
        val parsedBackendError = parseBackendError(errorBody = errorBody, statusCode = statusCode)
        val errorResponse =
            try {
                json.decodeFromString(CheckoutErrorResponseDto.serializer(), errorBody)
            } catch (_: Exception) {
                null
            }

        return if (errorResponse != null) {
            val resolvedMessage =
                parsedBackendError?.message
                    ?: errorResponse.messageToPurchaser.ifEmpty {
                        errorResponse.message.ifEmpty { errorBody }
                    }
            Result.failure(
                CheckoutApiException(
                    CheckoutError.ApiError(
                        code = errorResponse.type.ifBlank { parsedBackendError?.code ?: "http_$statusCode" },
                        message = resolvedMessage,
                    ),
                ),
            )
        } else if (parsedBackendError != null) {
            Result.failure(
                CheckoutApiException(
                    CheckoutError.ApiError(
                        code = parsedBackendError.code,
                        message = parsedBackendError.message,
                    ),
                ),
            )
        } else {
            Result.failure(
                CheckoutApiException(
                    CheckoutError.NetworkError("HTTP $statusCode: $errorBody"),
                ),
            )
        }
    }

    private fun Throwable.asNetworkErrorMessage(defaultMessage: String): String {
        val type = this::class.simpleName ?: "Exception"
        val directMessage = message?.trim().orEmpty()
        val causeMessage = cause?.message?.trim().orEmpty()

        return when {
            directMessage.isNotEmpty() -> "$type: $directMessage"
            causeMessage.isNotEmpty() -> "$type (cause: $causeMessage)"
            else -> "$type: $defaultMessage"
        }
    }

    private fun parseBackendError(
        errorBody: String,
        statusCode: Int,
    ): ParsedBackendError? {
        val rootObject =
            try {
                json.parseToJsonElement(errorBody).jsonObject
            } catch (_: Exception) {
                return null
            }

        val topLevelMessage = rootObject.stringValue("message")
        val detailMessage = extractDetailMessage(rootObject["details"])
        val resolvedMessage = detailMessage ?: topLevelMessage ?: return null

        val code =
            rootObject.stringValue("status")
                ?: rootObject.stringValue("code")
                ?: "http_$statusCode"

        return ParsedBackendError(code = code, message = resolvedMessage)
    }

    private fun extractDetailMessage(detailsElement: JsonElement?): String? = extractMessageRecursively(detailsElement)

    private fun extractMessageRecursively(element: JsonElement?): String? =
        when (element) {
            is JsonObject -> {
                element.stringValue("message")
                    ?: element.stringValue("debug_message")
                    ?: extractMessageRecursively(element["details"])
            }
            is JsonArray -> {
                element.firstNotNullOfOrNull { extractMessageRecursively(it) }
            }
            is JsonPrimitive -> element.contentOrNull?.trim()?.takeIf { it.isNotEmpty() }
            else -> null
        }

    private fun JsonObject.stringValue(key: String): String? {
        val primitive = this[key] as? JsonPrimitive ?: return null
        return primitive.contentOrNull?.trim()?.takeIf { it.isNotEmpty() }
    }

    private data class ParsedBackendError(
        val code: String,
        val message: String,
    )
}
