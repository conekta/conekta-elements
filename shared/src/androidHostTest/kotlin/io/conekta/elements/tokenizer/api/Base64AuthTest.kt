package io.conekta.elements.tokenizer.api

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Base64AuthTest {
    @Test
    fun encodesPublicKeyWithColonSuffix() {
        val encoded = base64EncodeAuth("key_test_abc123:")
        // "key_test_abc123:" -> base64
        val expected =
            java.util.Base64
                .getEncoder()
                .encodeToString("key_test_abc123:".toByteArray(Charsets.UTF_8))
        assertEquals(expected, encoded)
    }

    @Test
    fun encodesEmptyString() {
        val encoded = base64EncodeAuth("")
        val expected =
            java.util.Base64
                .getEncoder()
                .encodeToString("".toByteArray(Charsets.UTF_8))
        assertEquals(expected, encoded)
    }

    @Test
    fun encodedResultIsValidBase64() {
        val encoded = base64EncodeAuth("key_GRtIP379Iat3XJcW93LYTpo:")
        // Should only contain valid base64 characters
        assertTrue(
            encoded.all {
                it in 'A'..'Z' ||
                    it in 'a'..'z' ||
                    it in '0'..'9' ||
                    it == '+' ||
                    it == '/' ||
                    it == '='
            },
        )
    }

    @Test
    fun encodedResultMatchesKnownValue() {
        // "key_test:" in base64 is "a2V5X3Rlc3Q6"
        val encoded = base64EncodeAuth("key_test:")
        assertEquals("a2V5X3Rlc3Q6", encoded)
    }
}
