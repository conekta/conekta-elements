@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)

package io.conekta.elements.tokenizer.crypto

import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.CoreCrypto.CCCrypt
import platform.CoreCrypto.CC_MD5
import platform.CoreCrypto.CC_MD5_DIGEST_LENGTH
import platform.CoreCrypto.kCCAlgorithmAES
import platform.CoreCrypto.kCCEncrypt
import platform.CoreCrypto.kCCOptionPKCS7Padding
import platform.CoreCrypto.kCCSuccess
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecKeyCreateEncryptedData
import platform.Security.SecKeyCreateWithData
import platform.Security.kSecAttrKeyClassPublic
import platform.Security.kSecAttrKeyTypeRSA
import platform.Security.kSecKeyAlgorithmRSAEncryptionPKCS1
import platform.posix.size_tVar

actual class CryptoService actual constructor() : CardEncryptor {
    actual override fun encryptCardData(
        cardJson: String,
        rsaPublicKeyBase64: String,
    ): EncryptedCardData {
        // 1. Generate random 16-byte AES key as hex string
        val aesKeyBytes = randomBytes(16)
        val aesKeyHex =
            aesKeyBytes.joinToString("") { byte ->
                val unsigned = byte.toInt() and 0xFF
                unsigned.toString(16).padStart(2, '0')
            }

        // 2. AES encrypt using CryptoJS-compatible passphrase mode
        val aesEncrypted = aesEncryptCryptoJs(cardJson, aesKeyHex)

        // 3. RSA encrypt the AES key
        val rsaEncrypted = rsaEncrypt(aesKeyHex, rsaPublicKeyBase64)

        // 4. Double base64 encode
        val doubleEncodedData = base64Encode(aesEncrypted.encodeToByteArray())
        val doubleEncodedKey = base64Encode(rsaEncrypted.encodeToByteArray())

        return EncryptedCardData(
            encryptedData = doubleEncodedData,
            encryptedKey = doubleEncodedKey,
        )
    }

    private fun aesEncryptCryptoJs(
        plaintext: String,
        passphrase: String,
    ): String {
        val salt = randomBytes(8)
        val (key, iv) = evpBytesToKey(passphrase.encodeToByteArray(), salt)
        val plaintextBytes = plaintext.encodeToByteArray()

        val ciphertext = aesCbcEncrypt(plaintextBytes, key, iv)

        // CryptoJS format: "Salted__" + salt + ciphertext → base64
        val salted = "Salted__".encodeToByteArray() + salt + ciphertext
        return base64Encode(salted)
    }

    @Suppress("MagicNumber")
    private fun evpBytesToKey(
        passphrase: ByteArray,
        salt: ByteArray,
    ): Pair<ByteArray, ByteArray> {
        val totalNeeded = 48 // 32 key + 16 IV
        val result = ByteArray(totalNeeded)
        var offset = 0
        var previousHash: ByteArray? = null

        while (offset < totalNeeded) {
            val input =
                (previousHash ?: ByteArray(0)) + passphrase + salt
            previousHash = md5(input)

            val toCopy = minOf(previousHash.size, totalNeeded - offset)
            previousHash.copyInto(result, offset, 0, toCopy)
            offset += toCopy
        }

        return Pair(result.copyOfRange(0, 32), result.copyOfRange(32, 48))
    }

    @Suppress("MagicNumber")
    private fun md5(data: ByteArray): ByteArray =
        memScoped {
            val digest = allocArray<UByteVar>(CC_MD5_DIGEST_LENGTH)
            data.usePinned { pinned ->
                CC_MD5(pinned.addressOf(0), data.size.convert(), digest)
            }
            digest.readBytes(CC_MD5_DIGEST_LENGTH)
        }

    private fun aesCbcEncrypt(
        data: ByteArray,
        key: ByteArray,
        iv: ByteArray,
    ): ByteArray =
        memScoped {
            // Maximum output size for PKCS7: input size + block size
            val bufferSize = data.size + 16
            val buffer = ByteArray(bufferSize)
            val dataOutMoved = alloc<size_tVar>()

            val status =
                data.usePinned { dataPinned ->
                    key.usePinned { keyPinned ->
                        iv.usePinned { ivPinned ->
                            buffer.usePinned { bufferPinned ->
                                CCCrypt(
                                    kCCEncrypt,
                                    kCCAlgorithmAES,
                                    kCCOptionPKCS7Padding,
                                    keyPinned.addressOf(0),
                                    key.size.convert(),
                                    ivPinned.addressOf(0),
                                    dataPinned.addressOf(0),
                                    data.size.convert(),
                                    bufferPinned.addressOf(0),
                                    bufferSize.convert(),
                                    dataOutMoved.ptr,
                                )
                            }
                        }
                    }
                }

            require(status == kCCSuccess) { "AES encryption failed with status: $status" }
            buffer.copyOfRange(0, dataOutMoved.value.toInt())
        }

    @Suppress("MagicNumber", "CAST_NEVER_SUCCEEDS")
    private fun rsaEncrypt(
        plaintext: String,
        publicKeyBase64: String,
    ): String {
        val keyData = base64Decode(publicKeyBase64)
        val nsKeyData =
            keyData.usePinned { pinned ->
                NSData.create(bytes = pinned.addressOf(0), length = keyData.size.convert())
            }

        val attributes =
            mapOf<Any?, Any?>(
                platform.Security.kSecAttrKeyType to kSecAttrKeyTypeRSA,
                platform.Security.kSecAttrKeyClass to kSecAttrKeyClassPublic,
                platform.Security.kSecAttrKeySizeInBits to 2048,
            )

        val secKey =
            SecKeyCreateWithData(
                nsKeyData as platform.CoreFoundation.CFDataRef,
                attributes as platform.CoreFoundation.CFDictionaryRef,
                null,
            ) ?: error("Failed to create RSA public key")

        val plaintextData =
            (plaintext as NSString).dataUsingEncoding(NSUTF8StringEncoding)
                ?: error("Failed to encode plaintext")

        val encryptedData =
            SecKeyCreateEncryptedData(
                secKey,
                kSecKeyAlgorithmRSAEncryptionPKCS1,
                plaintextData as platform.CoreFoundation.CFDataRef,
                null,
            ) ?: error("RSA encryption failed")

        return (encryptedData as NSData).base64EncodedStringWithOptions(0u)
    }

    private fun base64Encode(data: ByteArray): String {
        val nsData =
            data.usePinned { pinned ->
                NSData.create(bytes = pinned.addressOf(0), length = data.size.convert())
            }
        return nsData.base64EncodedStringWithOptions(0u)
    }

    private fun base64Decode(base64String: String): ByteArray {
        val nsData =
            NSData.create(base64EncodedString = base64String, options = 0u)
                ?: error("Invalid base64 string")
        return nsData.toByteArray()
    }

    private fun randomBytes(count: Int): ByteArray {
        val bytes = ByteArray(count)
        bytes.usePinned { pinned ->
            platform.Security.SecRandomCopyBytes(
                platform.Security.kSecRandomDefault,
                count.convert(),
                pinned.addressOf(0),
            )
        }
        return bytes
    }

    private fun NSData.toByteArray(): ByteArray {
        val size = this.length.toInt()
        if (size == 0) return ByteArray(0)
        val bytes = ByteArray(size)
        bytes.usePinned { pinned ->
            platform.posix.memcpy(
                pinned.addressOf(0),
                this.bytes,
                this.length,
            )
        }
        return bytes
    }
}
