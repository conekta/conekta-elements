package io.conekta.elements.tokenizer.crypto

/**
 * Interface for RSA encryption, enabling testing with fakes.
 *
 * Mirrors conekta-ios Card.m's `encryptWithPublicKey:` — encrypts one value at a time.
 */
fun interface CardEncryptor {
    fun encrypt(
        plaintext: String,
        rsaPublicKeyBase64: String,
    ): String
}

/**
 * Platform-specific RSA PKCS#1 v1.5 encryption service.
 *
 * The public key is DER-encoded, base64, without PEM headers
 * (same key used in conekta-ios).
 */
expect class CryptoService() : CardEncryptor {
    override fun encrypt(
        plaintext: String,
        rsaPublicKeyBase64: String,
    ): String
}
