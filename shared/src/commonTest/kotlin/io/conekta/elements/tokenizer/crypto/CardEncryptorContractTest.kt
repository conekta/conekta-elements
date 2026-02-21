package io.conekta.elements.tokenizer.crypto

import kotlin.test.Test
import kotlin.test.assertEquals

class CardEncryptorContractTest {
    @Test
    fun fakeEncryptorReturnsExpectedValue() {
        val fake = CardEncryptor { plaintext, _ -> "encrypted:$plaintext" }

        val result = fake.encrypt("4242424242424242", "testKey")
        assertEquals("encrypted:4242424242424242", result)
    }

    @Test
    fun fakeEncryptorReceivesRsaKey() {
        var capturedKey = ""
        val fake =
            CardEncryptor { _, rsaKey ->
                capturedKey = rsaKey
                "enc"
            }

        fake.encrypt("value", "my_rsa_key")
        assertEquals("my_rsa_key", capturedKey)
    }
}
