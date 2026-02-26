package io.conekta.compose.components.cash

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_cash_provider_more_link
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.theme.ConektaTheme
import org.jetbrains.compose.resources.stringResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CashProviderComponentsTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cashProviderMoreLinkDisplaysAndCallsOnClick() =
        runComposeUiTest {
            var clicked = false
            var moreLinkText = ""
            setContent {
                moreLinkText = stringResource(Res.string.checkout_cash_provider_more_link)
                ConektaTheme {
                    CashProviderMoreLink(onClick = { clicked = true })
                }
            }

            onNodeWithText(moreLinkText).assertIsDisplayed().performClick()

            assertTrue(clicked)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cashProviderLogoImageRendersWithoutCrash() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CashProviderLogoImage(url = "https://invalid-url.test/logo.png")
                }
            }

            onRoot().assertExists()
        }
}
