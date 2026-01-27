package io.conekta.elements

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Androide ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()
