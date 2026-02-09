package io.conekta.compose

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import coil3.svg.SvgDecoder
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ConektaImageLoaderTest {
    private val context: Application = ApplicationProvider.getApplicationContext()

    @Test
    fun newImageLoader_returnsNonNullImageLoader() {
        val imageLoader = ConektaImageLoader.newImageLoader(context)
        assertNotNull(imageLoader)
    }

    @Test
    fun newImageLoader_containsSvgDecoder() {
        val imageLoader = ConektaImageLoader.newImageLoader(context)
        val hasSvgDecoder =
            imageLoader.components.decoderFactories.any { it is SvgDecoder.Factory }
        assertTrue("ImageLoader should contain SvgDecoder.Factory", hasSvgDecoder)
    }

    @Test
    fun newImageLoader_containsNetworkFetcher() {
        val imageLoader = ConektaImageLoader.newImageLoader(context)
        val hasNetworkFetcher =
            imageLoader.components.fetcherFactories.any {
                it.first::class.qualifiedName == "coil3.network.NetworkFetcher.Factory"
            }
        assertTrue("ImageLoader should contain NetworkFetcher.Factory", hasNetworkFetcher)
    }
}
