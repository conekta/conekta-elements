package io.conekta.elements.checkout.api

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

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
}
