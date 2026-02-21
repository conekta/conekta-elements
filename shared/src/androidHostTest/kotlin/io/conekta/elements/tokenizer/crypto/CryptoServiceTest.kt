package io.conekta.elements.tokenizer.crypto

import java.security.KeyPairGenerator
import java.util.Base64
import javax.crypto.Cipher
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CryptoServiceTest {
    private val cryptoService = CryptoService()

    private val rsaKeyPair by lazy {
        KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
    }

    private val rsaPublicKeyBase64 by lazy {
        Base64.getEncoder().encodeToString(rsaKeyPair.public.encoded)
    }

    @Test
    fun encryptReturnsNonEmptyBase64() {
        val result = cryptoService.encrypt("4242424242424242", rsaPublicKeyBase64)
        assertTrue(result.isNotEmpty())
        assertTrue(Base64.getDecoder().decode(result).isNotEmpty())
    }

    @Test
    fun encryptRoundtripDecrypt() {
        val original = "4242424242424242"

        val encrypted = cryptoService.encrypt(original, rsaPublicKeyBase64)

        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, rsaKeyPair.private)
        val decrypted = String(cipher.doFinal(Base64.getDecoder().decode(encrypted)), Charsets.UTF_8)

        assertTrue(decrypted == original)
    }

    @Test
    fun eachCallProducesDifferentOutput() {
        // RSA PKCS1 includes random padding so outputs differ per call
        val result1 = cryptoService.encrypt("4242424242424242", rsaPublicKeyBase64)
        val result2 = cryptoService.encrypt("4242424242424242", rsaPublicKeyBase64)
        assertNotEquals(result1, result2)
    }

    @Test
    fun encryptsEachFieldIndependently() {
        val number = cryptoService.encrypt("4242424242424242", rsaPublicKeyBase64)
        val cvc = cryptoService.encrypt("123", rsaPublicKeyBase64)
        val expMonth = cryptoService.encrypt("12", rsaPublicKeyBase64)
        val expYear = cryptoService.encrypt("26", rsaPublicKeyBase64)
        val name = cryptoService.encrypt("John Doe", rsaPublicKeyBase64)

        // Each field decrypts to its original value
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, rsaKeyPair.private)

        fun decrypt(enc: String) = String(cipher.doFinal(Base64.getDecoder().decode(enc)), Charsets.UTF_8)

        assertTrue(decrypt(number) == "4242424242424242")
        assertTrue(decrypt(cvc) == "123")
        assertTrue(decrypt(expMonth) == "12")
        assertTrue(decrypt(expYear) == "26")
        assertTrue(decrypt(name) == "John Doe")
    }
}
