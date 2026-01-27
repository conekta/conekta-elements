package io.conekta.elements

class JsPlatform : Platform {
    override val name: String = "Web with Kotlin/JS, react"
}

actual fun getPlatform(): Platform = JsPlatform()
