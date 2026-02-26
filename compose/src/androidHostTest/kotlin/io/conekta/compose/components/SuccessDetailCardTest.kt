package io.conekta.compose.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.theme.ConektaTheme
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SuccessDetailCardTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun successDetailCardDisplaysDetailText() =
        runComposeUiTest {
            val detail = "Test Merchant"

            setContent {
                ConektaTheme {
                    SuccessDetailCard(detail = detail)
                }
            }

            onNodeWithText(detail).assertIsDisplayed()
        }
}
