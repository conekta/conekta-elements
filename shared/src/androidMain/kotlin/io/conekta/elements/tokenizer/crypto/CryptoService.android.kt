package io.conekta.elements.tokenizer.crypto

import android.util.Base64
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

actual class CryptoService actual constructor() : CardEncryptor {
    actual override fun encrypt(
        plaintext: String,
        rsaPublicKeyBase64: String,
    ): String {
        val keyBytes = Base64.decode(rsaPublicKeyBase64, Base64.DEFAULT)
        val publicKey = KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(keyBytes))
        // PKCS#1 v1.5 padding is required for compatibility with the Conekta API.
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding") // NOSONAR (java:S5547)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return Base64.encodeToString(cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8)), Base64.NO_WRAP)
    }
}
