package io.conekta.elements.tokenizer.api

import io.conekta.elements.tokenizer.models.TokenizerError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TokenizerApiExceptionTest {
    @Test
    fun messageFromApiError() {
        val error = TokenizerError.ApiError(code = "422", message = "Card is invalid")
        val exception = TokenizerApiException(error)
        assertEquals("Card is invalid", exception.message)
        assertIs<TokenizerError.ApiError>(exception.tokenizerError)
    }

    @Test
    fun messageFromNetworkError() {
        val error = TokenizerError.NetworkError("Connection timeout")
        val exception = TokenizerApiException(error)
        assertEquals("Connection timeout", exception.message)
        assertIs<TokenizerError.NetworkError>(exception.tokenizerError)
    }

    @Test
    fun messageFromValidationError() {
        val error = TokenizerError.ValidationError("Invalid card number")
        val exception = TokenizerApiException(error)
        assertEquals("Invalid card number", exception.message)
        assertIs<TokenizerError.ValidationError>(exception.tokenizerError)
    }

    @Test
    fun isExceptionSubclass() {
        val exception =
            TokenizerApiException(
                TokenizerError.NetworkError("test"),
            )
        assertIs<Exception>(exception)
    }

    @Test
    fun tokenizerErrorIsPreserved() {
        val apiError = TokenizerError.ApiError("invalid_request_error", "The card could not be processed")
        val exception = TokenizerApiException(apiError)
        val recovered = exception.tokenizerError as TokenizerError.ApiError
        assertEquals("invalid_request_error", recovered.code)
        assertEquals("The card could not be processed", recovered.message)
    }
}
