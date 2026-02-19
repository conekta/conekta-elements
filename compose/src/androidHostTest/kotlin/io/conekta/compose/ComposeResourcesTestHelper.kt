package io.conekta.compose

import androidx.test.core.app.ApplicationProvider
import org.robolectric.Robolectric

/**
 * Initializes the Compose resources Android context for Robolectric host tests.
 *
 * Compose Multiplatform 1.9.x uses a ContentProvider (AndroidContextProvider) to supply
 * the Android context to the resource reader. Robolectric does not always invoke it
 * automatically in library host tests, so we bootstrap it explicitly before each test.
 */
@Suppress("UNCHECKED_CAST")
fun initComposeResourcesContext() {
    val providerClass = Class.forName("org.jetbrains.compose.resources.AndroidContextProvider")
        as Class<android.content.ContentProvider>
    val context = ApplicationProvider.getApplicationContext<android.app.Application>()
    Robolectric.buildContentProvider(providerClass)
        .create("${context.packageName}.resources.AndroidContextProvider")
}
