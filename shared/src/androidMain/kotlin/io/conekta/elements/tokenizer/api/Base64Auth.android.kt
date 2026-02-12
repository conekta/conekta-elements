package io.conekta.elements.tokenizer.api

internal actual fun base64EncodeAuth(input: String): String =
    java.util.Base64
        .getEncoder()
        .encodeToString(input.toByteArray(Charsets.UTF_8))
