@file:Suppress("ktlint:standard:filename", "ktlint:standard:class-naming")

package io.conekta.elements.tokenizer.crypto

/**
 * Lazily loads jsencrypt via dynamic require() to avoid "window is not defined"
 * errors in Node.js test environments. The jsencrypt UMD bundle accesses window
 * at module init time, so we defer loading until actually needed at runtime.
 */
private fun createJSEncryptInstance(): dynamic = js("new (require('jsencrypt').JSEncrypt)()")

actual class CryptoService actual constructor() : CardEncryptor {
    // Cache the encryptor instance and key — encrypt() is called once per card field
    // (5 times per tokenization) always with the same RSA key.
    private var encryptor: dynamic = null
    private var currentKey: String = ""

    actual override fun encrypt(
        plaintext: String,
        rsaPublicKeyBase64: String,
    ): String {
        if (encryptor == null || currentKey != rsaPublicKeyBase64) {
            encryptor = createJSEncryptInstance()
            encryptor.setPublicKey("-----BEGIN PUBLIC KEY-----\n$rsaPublicKeyBase64\n-----END PUBLIC KEY-----")
            currentKey = rsaPublicKeyBase64
        }
        return (encryptor.encrypt(plaintext) as? String)
            ?: error("RSA encryption failed")
    }
}
