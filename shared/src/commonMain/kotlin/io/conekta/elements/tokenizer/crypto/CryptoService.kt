package io.conekta.elements.tokenizer.crypto

/**
 * Result of encrypting card data with AES + RSA.
 *
 * @property encryptedData AES-encrypted card JSON, double-base64 encoded.
 * @property encryptedKey  RSA-encrypted AES key, double-base64 encoded.
 */
data class EncryptedCardData(
    val encryptedData: String,
    val encryptedKey: String,
)

/**
 * Interface for card data encryption, enabling testing with fakes.
 */
interface CardEncryptor {
    fun encryptCardData(
        cardJson: String,
        rsaPublicKeyBase64: String,
    ): EncryptedCardData
}

/**
 * Platform-specific crypto service that replicates the CryptoJS passphrase-mode
 * encryption flow used by int-payment-component:
 *
 * 1. Generate random 16-byte AES key → hex string (32 chars)
 * 2. AES-256-CBC encrypt using CryptoJS-compatible EVP_BytesToKey key derivation
 * 3. RSA PKCS#1 v1.5 encrypt the hex AES key
 * 4. Double-base64 encode both outputs
 */
expect class CryptoService() : CardEncryptor {
    /**
     * Encrypts [cardJson] using AES (CryptoJS-compatible) and wraps the
     * AES key with [rsaPublicKeyBase64] (DER-encoded, base64, no headers).
     */
    override fun encryptCardData(
        cardJson: String,
        rsaPublicKeyBase64: String,
    ): EncryptedCardData
}
