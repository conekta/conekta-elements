package com.conekta.example

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import io.conekta.compose.ConektaImageLoader

class
ExampleApplication : Application(), SingletonImageLoader.Factory {
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ConektaImageLoader.newImageLoader(context)
}
