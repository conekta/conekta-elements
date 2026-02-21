@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)

package io.conekta.elements.tokenizer.crypto

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionarySetValue
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.kCFAllocatorDefault
import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create
import platform.Security.SecKeyAlgorithm
import platform.Security.SecKeyCopyExternalRepresentation
import platform.Security.SecKeyCopyPublicKey
import platform.Security.SecKeyCreateDecryptedData
import platform.Security.SecKeyCreateRandomKey
import platform.Security.kSecAttrKeyClass
import platform.Security.kSecAttrKeyClassPrivate
import platform.Security.kSecAttrKeySizeInBits
import platform.Security.kSecAttrKeyType
import platform.Security.kSecAttrKeyTypeRSA
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CryptoServiceTest {
    private val cryptoService = CryptoService()

    private val rsaKeyPair by lazy { generateRsaKeyPair() }

    private val rsaPublicKeyBase64 by lazy { exportPublicKeyBase64(rsaKeyPair.first) }

    @Test
    fun encryptReturnsNonEmptyBase64() {
        val result = cryptoService.encrypt("4242424242424242", rsaPublicKeyBase64)
        assertTrue(result.isNotEmpty())
        val decoded = base64Decode(result)
        assertTrue(decoded.isNotEmpty())
    }

    @Test
    fun encryptRoundtripDecrypt() {
        val original = "4242424242424242"

        val encrypted = cryptoService.encrypt(original, rsaPublicKeyBase64)
        val decrypted = rsaDecrypt(encrypted, rsaKeyPair.second)

        assertEquals(original, decrypted)
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

        assertEquals("4242424242424242", rsaDecrypt(number, rsaKeyPair.second))
        assertEquals("123", rsaDecrypt(cvc, rsaKeyPair.second))
        assertEquals("12", rsaDecrypt(expMonth, rsaKeyPair.second))
        assertEquals("26", rsaDecrypt(expYear, rsaKeyPair.second))
        assertEquals("John Doe", rsaDecrypt(name, rsaKeyPair.second))
    }

    // -- helpers ----------------------------------------------------------

    /** Generates an RSA 2048-bit key pair and returns (publicSecKey, privateSecKey). */
    private fun generateRsaKeyPair(): Pair<platform.Security.SecKeyRef, platform.Security.SecKeyRef> =
        memScoped {
            val attributes =
                CFDictionaryCreateMutable(kCFAllocatorDefault, 3.convert(), null, null)
                    ?: error("Failed to create attributes dictionary")
            CFDictionarySetValue(attributes, kSecAttrKeyType, kSecAttrKeyTypeRSA)
            val keySizeVar = alloc<kotlinx.cinterop.IntVar>()
            keySizeVar.value = 2048
            @Suppress("UNCHECKED_CAST")
            CFDictionarySetValue(
                attributes,
                kSecAttrKeySizeInBits,
                platform.CoreFoundation.CFNumberCreate(
                    kCFAllocatorDefault,
                    platform.CoreFoundation.kCFNumberSInt32Type,
                    keySizeVar.ptr,
                ),
            )
            CFDictionarySetValue(attributes, kSecAttrKeyClass, kSecAttrKeyClassPrivate)

            val privateKey = SecKeyCreateRandomKey(attributes, null)
            CFRelease(attributes)
            privateKey ?: error("Failed to generate RSA key pair")

            val publicKey = SecKeyCopyPublicKey(privateKey) ?: error("Failed to extract public key")
            Pair(publicKey, privateKey)
        }

    /** Exports the public key as base64-encoded DER. */
    private fun exportPublicKeyBase64(publicKey: platform.Security.SecKeyRef): String {
        val cfData =
            SecKeyCopyExternalRepresentation(publicKey, null)
                ?: error("Failed to export public key")
        val length = CFDataGetLength(cfData).toInt()
        val ptr = CFDataGetBytePtr(cfData) ?: error("Failed to get key bytes")
        val bytes = ByteArray(length)
        bytes.usePinned { pinned ->
            platform.posix.memcpy(pinned.addressOf(0), ptr, length.convert())
        }
        CFRelease(cfData)
        val nsData =
            bytes.usePinned { pinned ->
                NSData.create(bytes = pinned.addressOf(0), length = bytes.size.convert())
            }
        return nsData.base64EncodedStringWithOptions(0u)
    }

    /** Decrypts a base64-encoded ciphertext using the private key with PKCS1 padding. */
    private fun rsaDecrypt(
        base64Ciphertext: String,
        privateKey: platform.Security.SecKeyRef,
    ): String {
        val cipherBytes = base64Decode(base64Ciphertext)
        val cfCipherData =
            cipherBytes.usePinned { pinned ->
                platform.CoreFoundation.CFDataCreate(
                    kCFAllocatorDefault,
                    pinned.addressOf(0).reinterpret(),
                    cipherBytes.size.convert(),
                )
            } ?: error("Failed to create cipher CFData")

        @Suppress("UNCHECKED_CAST")
        val algorithm = platform.Security.kSecKeyAlgorithmRSAEncryptionPKCS1 as SecKeyAlgorithm
        val decryptedCfData = SecKeyCreateDecryptedData(privateKey, algorithm, cfCipherData, null)
        CFRelease(cfCipherData)
        decryptedCfData ?: error("RSA decryption failed")

        val length = CFDataGetLength(decryptedCfData).toInt()
        val ptr = CFDataGetBytePtr(decryptedCfData) ?: error("Failed to get decrypted bytes")
        val bytes = ByteArray(length)
        bytes.usePinned { pinned ->
            platform.posix.memcpy(pinned.addressOf(0), ptr, length.convert())
        }
        CFRelease(decryptedCfData)
        return bytes.decodeToString()
    }

    private fun base64Decode(base64String: String): ByteArray {
        val nsData =
            NSData.create(base64EncodedString = base64String, options = 0u)
                ?: error("Invalid base64 string")
        return nsDataToByteArray(nsData)
    }

    private fun nsDataToByteArray(nsData: NSData): ByteArray {
        val size = nsData.length.toInt()
        if (size == 0) return ByteArray(0)
        val bytes = ByteArray(size)
        bytes.usePinned { pinned ->
            platform.posix.memcpy(pinned.addressOf(0), nsData.bytes, nsData.length)
        }
        return bytes
    }
}
