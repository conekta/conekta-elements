package io.conekta.compose

import android.content.Context
import coil3.ImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.svg.SvgDecoder

/**
 * Creates a pre-configured [ImageLoader] for Conekta Elements.
 *
 * Includes:
 * - Ktor network fetcher for CDN image loading
 * - SVG decoder for card brand logos
 *
 * ## Usage
 *
 * ```kotlin
 * class MyApp : Application(), SingletonImageLoader.Factory {
 *     override fun newImageLoader(context: Context): ImageLoader {
 *         return ConektaImageLoader.newImageLoader(context)
 *     }
 * }
 * ```
 *
 * Then register in your AndroidManifest.xml:
 * ```xml
 * <application android:name=".MyApp" ...>
 * ```
 */
object ConektaImageLoader {
    fun newImageLoader(context: Context): ImageLoader =
        ImageLoader
            .Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
                add(SvgDecoder.Factory())
            }.build()
}
