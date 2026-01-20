package io.conekta.elements

class JsPlatform: Platform {
    override val name: String = "Web with Kotlin/JS, react papa"
}

actual fun getPlatform(): Platform = JsPlatform()