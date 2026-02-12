@file:Suppress("ktlint:standard:filename", "ktlint:standard:class-naming")

package io.conekta.elements.tokenizer.crypto

// --- External declarations for crypto-js ---

@JsModule("crypto-js")
@JsNonModule
external object CryptoJS {
    object AES {
        fun encrypt(
            message: dynamic,
            key: dynamic,
        ): dynamic
    }

    object lib {
        object WordArray {
            fun random(nBytes: Int): dynamic
        }
    }
}

// --- External declarations for jsencrypt ---

@JsModule("jsencrypt")
@JsNonModule
external class JSEncrypt {
    fun setPublicKey(publicKey: String)

    fun encrypt(message: String): dynamic
}

// --- btoa polyfill for Node.js environments ---

private fun safeBtoa(input: String): String =
    js(
        """
    (typeof btoa !== 'undefined')
        ? btoa(input)
        : Buffer.from(input, 'binary').toString('base64')
    """,
    )

actual class CryptoService actual constructor() : CardEncryptor {
    actual override fun encryptCardData(
        cardJson: String,
        rsaPublicKeyBase64: String,
    ): EncryptedCardData {
        // 1. Generate random 16-byte AES key as hex string — same as CryptoJS.lib.WordArray.random(16).toString()
        val aesKeyWordArray = CryptoJS.lib.WordArray.random(16)
        val aesKeyHex: String = aesKeyWordArray.toString()

        // 2. AES encrypt — CryptoJS passphrase mode (identical to int-payment-component)
        val aesEncrypted: String = CryptoJS.AES.encrypt(cardJson, aesKeyHex).toString()

        // 3. RSA encrypt the AES key hex
        val encryptor = JSEncrypt()
        encryptor.setPublicKey("-----BEGIN PUBLIC KEY-----\n$rsaPublicKeyBase64\n-----END PUBLIC KEY-----")
        val rsaEncrypted: String =
            encryptor.encrypt(aesKeyHex) as? String
                ?: error("RSA encryption failed")

        // 4. Double base64 — matching Base64Tokenization.encode() from int-payment-component
        val doubleEncodedData = safeBtoa(aesEncrypted)
        val doubleEncodedKey = safeBtoa(rsaEncrypted)

        return EncryptedCardData(
            encryptedData = doubleEncodedData,
            encryptedKey = doubleEncodedKey,
        )
    }
}
