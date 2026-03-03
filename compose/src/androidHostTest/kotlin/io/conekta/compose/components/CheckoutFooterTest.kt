package io.conekta.compose.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.theme.ConektaTheme
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class CheckoutFooterTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutFooterShowsSelectedLanguageTag() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CheckoutFooter(
                        selectedLanguageTag = "es",
                        onLanguageSelected = {},
                    )
                }
            }

            onNodeWithText("ES").assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutFooterCallsOnLanguageSelectedWhenUserChoosesOption() =
        runComposeUiTest {
            var selectedLanguage: String? = null

            setContent {
                ConektaTheme {
                    CheckoutFooter(
                        selectedLanguageTag = "es",
                        onLanguageSelected = { selectedLanguage = it },
                    )
                }
            }

            onNodeWithText("ES").performClick()
            onNodeWithText("EN").performClick()

            assertEquals("en", selectedLanguage)
        }
}
