package io.conekta.elements.tokenizer.crypto

import java.security.KeyFactory
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

actual class CryptoService actual constructor() : CardEncryptor {
    private val secureRandom = SecureRandom()
    private val base64Encoder = Base64.getEncoder()
    private val base64Decoder = Base64.getDecoder()

    actual override fun encryptCardData(
        cardJson: String,
        rsaPublicKeyBase64: String,
    ): EncryptedCardData {
        // 1. Generate random 16-byte AES key as hex string (32 chars) — same as CryptoJS.lib.WordArray.random(16).toString()
        val aesKeyBytes = ByteArray(16)
        secureRandom.nextBytes(aesKeyBytes)
        val aesKeyHex = aesKeyBytes.joinToString("") { "%02x".format(it) }

        // 2. AES encrypt using CryptoJS-compatible passphrase mode (EVP_BytesToKey)
        val aesEncrypted = aesEncryptCryptoJs(cardJson, aesKeyHex)

        // 3. RSA encrypt the AES key hex string
        val rsaEncrypted = rsaEncrypt(aesKeyHex, rsaPublicKeyBase64)

        // 4. Double base64: the AES output is already base64, and RSA output is already base64
        //    Now we base64-encode those base64 strings (matching Base64Tokenization.encode)
        val doubleEncodedData = base64Encode(aesEncrypted.toByteArray(Charsets.UTF_8))
        val doubleEncodedKey = base64Encode(rsaEncrypted.toByteArray(Charsets.UTF_8))

        return EncryptedCardData(
            encryptedData = doubleEncodedData,
            encryptedKey = doubleEncodedKey,
        )
    }

    /**
     * Replicates CryptoJS.AES.encrypt(plaintext, passphrase).toString()
     *
     * CryptoJS passphrase mode:
     * 1. Generate random 8-byte salt
     * 2. EVP_BytesToKey(MD5, passphrase, salt) → 32-byte key + 16-byte IV
     * 3. AES-256-CBC with PKCS7 padding
     * 4. Output = Base64("Salted__" + salt + ciphertext)
     */
    private fun aesEncryptCryptoJs(
        plaintext: String,
        passphrase: String,
    ): String {
        val salt = ByteArray(8)
        secureRandom.nextBytes(salt)

        val (key, iv) = evpBytesToKey(passphrase.toByteArray(Charsets.UTF_8), salt)

        // AES-256-CBC is required for CryptoJS passphrase-mode compatibility with the Conekta BFF.
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding") // NOSONAR (java:S5542)
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(iv))
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

        // CryptoJS format: "Salted__" + salt + ciphertext → base64
        val salted = "Salted__".toByteArray(Charsets.UTF_8) + salt + ciphertext
        return base64Encode(salted)
    }

    /**
     * OpenSSL EVP_BytesToKey with MD5, producing 32 bytes key + 16 bytes IV (48 bytes total).
     */
    private fun evpBytesToKey(
        passphrase: ByteArray,
        salt: ByteArray,
    ): Pair<ByteArray, ByteArray> {
        // MD5 is required by OpenSSL EVP_BytesToKey for CryptoJS passphrase-mode compatibility with the Conekta BFF.
        val md5 = MessageDigest.getInstance("MD5") // NOSONAR (java:S4790)
        val totalNeeded = 48 // 32 (key) + 16 (IV)
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

    /**
     * RSA PKCS#1 v1.5 encrypt.
     */
    private fun rsaEncrypt(
        plaintext: String,
        publicKeyBase64: String,
    ): String {
        val keyBytes = base64Decoder.decode(publicKeyBase64)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec)

        // PKCS#1 v1.5 padding is required for compatibility with the Conekta BFF.
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding") // NOSONAR (java:S5547)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encrypted = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        return base64Encode(encrypted)
    }

    private fun base64Encode(data: ByteArray): String = base64Encoder.encodeToString(data)
}
