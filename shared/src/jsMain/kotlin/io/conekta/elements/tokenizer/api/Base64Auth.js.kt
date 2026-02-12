package io.conekta.elements.tokenizer.api

internal actual fun base64EncodeAuth(input: String): String =
    js(
        """
    (typeof btoa !== 'undefined')
        ? btoa(unescape(encodeURIComponent(input)))
        : Buffer.from(input, 'utf-8').toString('base64')
    """,
    )
