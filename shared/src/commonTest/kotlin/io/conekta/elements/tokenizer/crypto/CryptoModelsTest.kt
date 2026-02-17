package io.conekta.elements.tokenizer.crypto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class EncryptedCardDataTest {
    @Test
    fun storesEncryptedDataAndKey() {
        val result =
            EncryptedCardData(
                encryptedData = "aes_encrypted_base64",
                encryptedKey = "rsa_encrypted_base64",
            )
        assertEquals("aes_encrypted_base64", result.encryptedData)
        assertEquals("rsa_encrypted_base64", result.encryptedKey)
    }

    @Test
    fun equalityForSameValues() {
        val a = EncryptedCardData("data1", "key1")
        val b = EncryptedCardData("data1", "key1")
        assertEquals(a, b)
    }

    @Test
    fun inequalityForDifferentData() {
        val a = EncryptedCardData("data1", "key1")
        val b = EncryptedCardData("data2", "key1")
        assertNotEquals(a, b)
    }

    @Test
    fun inequalityForDifferentKey() {
        val a = EncryptedCardData("data1", "key1")
        val b = EncryptedCardData("data1", "key2")
        assertNotEquals(a, b)
    }

    @Test
    fun copyModifiesOnlySpecifiedField() {
        val original = EncryptedCardData("data", "key")
        val copy = original.copy(encryptedData = "newData")
        assertEquals("newData", copy.encryptedData)
        assertEquals("key", copy.encryptedKey)
    }
}

class CardEncryptorContractTest {
    @Test
    fun fakeEncryptorReturnsExpectedValues() {
        val fake =
            object : CardEncryptor {
                override fun encryptCardData(
                    cardJson: String,
                    rsaPublicKeyBase64: String,
                ): EncryptedCardData =
                    EncryptedCardData(
                        encryptedData = "encrypted:$cardJson",
                        encryptedKey = "key:$rsaPublicKeyBase64",
                    )
            }

        val result = fake.encryptCardData("{\"card\":{}}", "testKey")
        assertEquals("encrypted:{\"card\":{}}", result.encryptedData)
        assertEquals("key:testKey", result.encryptedKey)
    }
}
