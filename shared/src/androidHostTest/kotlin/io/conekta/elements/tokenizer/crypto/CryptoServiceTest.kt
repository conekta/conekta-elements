package io.conekta.elements.tokenizer.crypto

import java.security.KeyPairGenerator
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CryptoServiceTest {
    private val cryptoService = CryptoService()

    // Generate a test RSA keypair for encrypt/decrypt roundtrip
    private val rsaKeyPair by lazy {
        KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
    }

    private val rsaPublicKeyBase64 by lazy {
        Base64.getEncoder().encodeToString(rsaKeyPair.public.encoded)
    }

    @Test
    fun encryptCardDataReturnsNonEmptyValues() {
        val result =
            cryptoService.encryptCardData(
                cardJson = """{"card":{"number":"4242424242424242"}}""",
                rsaPublicKeyBase64 = rsaPublicKeyBase64,
            )

        assertTrue(result.encryptedData.isNotEmpty(), "encryptedData should not be empty")
        assertTrue(result.encryptedKey.isNotEmpty(), "encryptedKey should not be empty")
    }

    @Test
    fun encryptCardDataProducesDoubleBase64EncodedData() {
        val result =
            cryptoService.encryptCardData(
                cardJson = """{"card":{"number":"4242424242424242"}}""",
                rsaPublicKeyBase64 = rsaPublicKeyBase64,
            )

        // Double base64: decode once should give valid base64, decode twice should give raw bytes
        val firstDecode = Base64.getDecoder().decode(result.encryptedData)
        assertTrue(firstDecode.isNotEmpty(), "First base64 decode should succeed")

        // The first decode should be a valid base64 string (the AES output)
        val firstDecodeStr = String(firstDecode, Charsets.UTF_8)
        val secondDecode = Base64.getDecoder().decode(firstDecodeStr)
        assertTrue(secondDecode.isNotEmpty(), "Second base64 decode should succeed")
    }

    @Test
    fun encryptCardDataProducesDoubleBase64EncodedKey() {
        val result =
            cryptoService.encryptCardData(
                cardJson = """{"card":{"number":"4242424242424242"}}""",
                rsaPublicKeyBase64 = rsaPublicKeyBase64,
            )

        // Decode the double-base64 key
        val firstDecode = Base64.getDecoder().decode(result.encryptedKey)
        assertTrue(firstDecode.isNotEmpty(), "First base64 decode of key should succeed")

        val firstDecodeStr = String(firstDecode, Charsets.UTF_8)
        val secondDecode = Base64.getDecoder().decode(firstDecodeStr)
        assertTrue(secondDecode.isNotEmpty(), "Second base64 decode of key should succeed")
    }

    @Test
    fun encryptedKeyCanBeDecryptedWithPrivateKey() {
        val result =
            cryptoService.encryptCardData(
                cardJson = """{"card":{"number":"test"}}""",
                rsaPublicKeyBase64 = rsaPublicKeyBase64,
            )

        // Undo double base64 to get the RSA ciphertext
        val singleBase64 = String(Base64.getDecoder().decode(result.encryptedKey), Charsets.UTF_8)
        val rsaCiphertext = Base64.getDecoder().decode(singleBase64)

        // Decrypt with private key
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, rsaKeyPair.private)
        val decryptedBytes = cipher.doFinal(rsaCiphertext)
        val aesKeyHex = String(decryptedBytes, Charsets.UTF_8)

        // The AES key should be a 32-char hex string (16 bytes as hex)
        assertEquals(32, aesKeyHex.length, "AES key should be 32 hex chars")
        assertTrue(aesKeyHex.all { it in '0'..'9' || it in 'a'..'f' }, "AES key should be hex")
    }

    @Test
    fun encryptedDataContainsSaltedPrefix() {
        val result =
            cryptoService.encryptCardData(
                cardJson = """{"test":"data"}""",
                rsaPublicKeyBase64 = rsaPublicKeyBase64,
            )

        // Undo double base64 to get the CryptoJS output
        val singleBase64 = String(Base64.getDecoder().decode(result.encryptedData), Charsets.UTF_8)
        val aesOutput = Base64.getDecoder().decode(singleBase64)

        // CryptoJS format: "Salted__" + 8-byte salt + ciphertext
        val prefix = String(aesOutput.copyOfRange(0, 8), Charsets.UTF_8)
        assertEquals("Salted__", prefix, "AES output should start with 'Salted__'")
        assertTrue(aesOutput.size > 16, "AES output should have salt + ciphertext after prefix")
    }

    @Test
    fun fullRoundtripDecrypt() {
        val originalJson = """{"card":{"number":"4242424242424242","cvc":"123"}}"""

        val result =
            cryptoService.encryptCardData(
                cardJson = originalJson,
                rsaPublicKeyBase64 = rsaPublicKeyBase64,
            )

        // Step 1: Recover AES key via RSA decrypt
        val singleBase64Key = String(Base64.getDecoder().decode(result.encryptedKey), Charsets.UTF_8)
        val rsaCiphertext = Base64.getDecoder().decode(singleBase64Key)
        val rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        rsaCipher.init(Cipher.DECRYPT_MODE, rsaKeyPair.private)
        val aesKeyHex = String(rsaCipher.doFinal(rsaCiphertext), Charsets.UTF_8)

        // Step 2: Recover AES ciphertext
        val singleBase64Data = String(Base64.getDecoder().decode(result.encryptedData), Charsets.UTF_8)
        val aesOutput = Base64.getDecoder().decode(singleBase64Data)

        // Step 3: Extract salt and ciphertext from CryptoJS format
        val salt = aesOutput.copyOfRange(8, 16)
        val ciphertext = aesOutput.copyOfRange(16, aesOutput.size)

        // Step 4: EVP_BytesToKey to derive key + IV
        val passphrase = aesKeyHex.toByteArray(Charsets.UTF_8)
        val derived = evpBytesToKey(passphrase, salt)
        val aesKey = derived.first
        val iv = derived.second

        // Step 5: AES decrypt
        val aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        aesCipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(aesKey, "AES"), IvParameterSpec(iv))
        val decryptedBytes = aesCipher.doFinal(ciphertext)
        val decryptedJson = String(decryptedBytes, Charsets.UTF_8)

        assertEquals(originalJson, decryptedJson, "Decrypted JSON should match original")
    }

    @Test
    fun eachEncryptionProducesDifferentOutput() {
        val cardJson = """{"card":{"number":"4242424242424242"}}"""

        val result1 = cryptoService.encryptCardData(cardJson, rsaPublicKeyBase64)
        val result2 = cryptoService.encryptCardData(cardJson, rsaPublicKeyBase64)

        // Due to random AES key and salt, outputs should differ
        assertNotEquals(result1.encryptedData, result2.encryptedData, "AES data should differ")
        assertNotEquals(result1.encryptedKey, result2.encryptedKey, "RSA key should differ")
    }

    /**
     * Replicates EVP_BytesToKey for test verification (same as CryptoService internal method).
     */
    private fun evpBytesToKey(
        passphrase: ByteArray,
        salt: ByteArray,
    ): Pair<ByteArray, ByteArray> {
        val md5 = java.security.MessageDigest.getInstance("MD5")
        val totalNeeded = 48
        val result = ByteArray(totalNeeded)
        var offset = 0
        var previousHash: ByteArray? = null

        while (offset < totalNeeded) {
            md5.reset()
            if (previousHash != null) {
                md5.update(previousHash)
            }
            md5.update(passphrase)
            md5.update(salt)
            previousHash = md5.digest()

            val toCopy = minOf(previousHash.size, totalNeeded - offset)
            previousHash.copyInto(result, offset, 0, toCopy)
            offset += toCopy
        }

        val key = result.copyOfRange(0, 32)
        val iv = result.copyOfRange(32, 48)
        return Pair(key, iv)
    }
}
