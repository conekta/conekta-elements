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
import platform.CoreFoundation.CFDataCreate
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionarySetValue
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.kCFAllocatorDefault
import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create
import platform.Security.SecKeyCreateWithData
import platform.Security.SecKeyEncrypt
import platform.Security.SecKeyGetBlockSize
import platform.Security.kSecAttrKeyClass
import platform.Security.kSecAttrKeyClassPublic
import platform.Security.kSecAttrKeyType
import platform.Security.kSecAttrKeyTypeRSA
import platform.Security.kSecPaddingPKCS1
import platform.posix.size_tVar

actual class CryptoService actual constructor() : CardEncryptor {
    actual override fun encrypt(
        plaintext: String,
        rsaPublicKeyBase64: String,
    ): String {
        val keyData = base64Decode(rsaPublicKeyBase64)
        val cfKeyData =
            keyData.usePinned { pinned ->
                CFDataCreate(kCFAllocatorDefault, pinned.addressOf(0).reinterpret(), keyData.size.convert())
            } ?: error("Failed to create key CFData")

        // CFDictionaryCreateMutable with null callbacks: safe for static CF constant keys/values
        // (kSecAttrKeyType, kSecAttrKeyTypeRSA, etc. are global singletons that never deallocate)
        val cfAttributes =
            CFDictionaryCreateMutable(kCFAllocatorDefault, 2.convert(), null, null)
                ?: error("Failed to create attributes dictionary")
        CFDictionarySetValue(cfAttributes, kSecAttrKeyType, kSecAttrKeyTypeRSA)
        CFDictionarySetValue(cfAttributes, kSecAttrKeyClass, kSecAttrKeyClassPublic)

        val secKey = SecKeyCreateWithData(cfKeyData, cfAttributes, null)
        CFRelease(cfKeyData)
        CFRelease(cfAttributes)
        secKey ?: error("Failed to create RSA public key")

        val plaintextBytes = plaintext.encodeToByteArray()
        val cipherBufferSize = SecKeyGetBlockSize(secKey).toInt()
        val cipherBuffer = ByteArray(cipherBufferSize)

        val actualSize =
            memScoped {
                val sizeVar = alloc<size_tVar>()
                sizeVar.value = cipherBufferSize.convert()
                val status =
                    plaintextBytes.usePinned { plainPinned ->
                        cipherBuffer.usePinned { cipherPinned ->
                            SecKeyEncrypt(
                                secKey,
                                kSecPaddingPKCS1,
                                plainPinned.addressOf(0).reinterpret(),
                                plaintextBytes.size.convert(),
                                cipherPinned.addressOf(0).reinterpret(),
                                sizeVar.ptr,
                            )
                        }
                    }
                require(status == 0) { "RSA encryption failed: $status" }
                sizeVar.value.toInt()
            }

        return base64Encode(cipherBuffer.copyOfRange(0, actualSize))
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

    private fun NSData.toByteArray(): ByteArray {
        val size = this.length.toInt()
        if (size == 0) return ByteArray(0)
        val bytes = ByteArray(size)
        bytes.usePinned { pinned ->
            platform.posix.memcpy(pinned.addressOf(0), this.bytes, this.length)
        }
        return bytes
    }
}
