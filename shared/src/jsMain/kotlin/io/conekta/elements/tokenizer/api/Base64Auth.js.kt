package io.conekta.elements.tokenizer.api

@Suppress("UNUSED_PARAMETER") // `input` is used inside the js() block via Kotlin/JS name binding
internal actual fun base64EncodeAuth(input: String): String =
    js(
        """
    (typeof btoa !== 'undefined')
        ? btoa(unescape(encodeURIComponent(input)))
        : Buffer.from(input, 'utf-8').toString('base64')
    """,
    )
