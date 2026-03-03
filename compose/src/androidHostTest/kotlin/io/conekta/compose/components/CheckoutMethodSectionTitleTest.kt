package io.conekta.compose.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_method_title
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.theme.ConektaTheme
import org.jetbrains.compose.resources.stringResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CheckoutMethodSectionTitleTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutMethodSectionTitleDisplaysLocalizedText() =
        runComposeUiTest {
            var methodTitle = ""
            setContent {
                methodTitle = stringResource(Res.string.checkout_method_title)
                ConektaTheme {
                    CheckoutMethodSectionTitle()
                }
            }

            onNodeWithText(methodTitle).assertIsDisplayed()
        }
}
