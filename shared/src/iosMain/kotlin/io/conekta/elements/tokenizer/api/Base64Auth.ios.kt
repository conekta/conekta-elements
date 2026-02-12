@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)

package io.conekta.elements.tokenizer.api

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create

internal actual fun base64EncodeAuth(input: String): String {
    val data = input.encodeToByteArray()
    val nsData =
        data.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = data.size.convert())
        }
    return nsData.base64EncodedStringWithOptions(0u)
}
