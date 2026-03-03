package io.conekta.elements.checkout.api

import io.conekta.elements.testfixtures.CheckoutApiFixtures
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CheckoutApiErrorMapperTest {
    private val mapper = CheckoutApiErrorMapper(Json { ignoreUnknownKeys = true })

    @Test
    fun mapExceptionToNetworkErrorUsesDirectMessageWhenPresent() {
        val throwable = IllegalStateException("socket timeout")

        val result = mapper.mapExceptionToNetworkError(throwable, defaultMessage = "fallback")

        assertContains(result.message, "IllegalStateException")
        assertContains(result.message, "socket timeout")
    }

    @Test
    fun mapExceptionToNetworkErrorUsesCauseMessageWhenDirectMessageIsEmpty() {
        val throwable = IllegalStateException("", RuntimeException("dns failed"))

        val result = mapper.mapExceptionToNetworkError(throwable, defaultMessage = "fallback")

        assertContains(result.message, "IllegalStateException")
        assertContains(result.message, "cause: dns failed")
    }

    @Test
    fun mapExceptionToNetworkErrorUsesDefaultWhenNoMessagesExist() {
        val throwable = IllegalStateException()

        val result = mapper.mapExceptionToNetworkError(throwable, defaultMessage = "Unknown network error")

        assertEquals("IllegalStateException: Unknown network error", result.message)
    }

    @Test
    fun buildHttpErrorResultUsesParsedBackendErrorWhenDtoDecodingFails() {
        val errorBody = CheckoutApiFixtures.malformedPurchaserMessageErrorPayload()

        val result = mapper.buildHttpErrorResult(statusCode = 422, errorBody = errorBody)

        assertTrue(result.isFailure)
        val exception = assertIs<CheckoutApiException>(result.exceptionOrNull())
        val apiError = assertIs<io.conekta.elements.checkout.models.CheckoutError.ApiError>(exception.checkoutError)
        assertEquals("unprocessable_entity", apiError.code)
        assertEquals("Backend validation failed", apiError.message)
    }

    @Test
    fun buildHttpErrorResultUsesParsedDetailMessageWhenDtoDecodingFails() {
        val errorBody = CheckoutApiFixtures.detailsOnlyMalformedPurchaserMessageErrorPayload()

        val result = mapper.buildHttpErrorResult(statusCode = 400, errorBody = errorBody)

        assertTrue(result.isFailure)
        val exception = assertIs<CheckoutApiException>(result.exceptionOrNull())
        val apiError = assertIs<io.conekta.elements.checkout.models.CheckoutError.ApiError>(exception.checkoutError)
        assertEquals("custom_error", apiError.code)
        assertEquals("First detail message", apiError.message)
    }
}
