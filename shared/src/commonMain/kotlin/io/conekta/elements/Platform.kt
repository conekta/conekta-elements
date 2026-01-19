package io.conekta.elements

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform