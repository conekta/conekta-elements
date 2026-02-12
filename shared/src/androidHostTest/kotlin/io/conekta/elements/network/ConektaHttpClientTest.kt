package io.conekta.elements.network

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ConektaHttpClientTest {
    @Test
    fun createReturnsNonNullClient() {
        val client = ConektaHttpClient.create()
        assertNotNull(client, "HttpClient should not be null")
    }

    @Test
    fun createReturnsDifferentInstances() {
        val client1 = ConektaHttpClient.create()
        val client2 = ConektaHttpClient.create()
        assertTrue(client1 !== client2, "Each call should create a new instance")
    }

    @Test
    fun clientCanBeClosed() {
        val client = ConektaHttpClient.create()
        client.close()
    }

    @Test
    fun multipleClientsCanBeCreatedAndClosed() {
        val clients = (1..3).map { ConektaHttpClient.create() }
        clients.forEach { it.close() }
    }
}
