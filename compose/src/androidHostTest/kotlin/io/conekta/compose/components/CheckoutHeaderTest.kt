package io.conekta.compose.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.content_description_security_info
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.theme.ConektaTheme
import org.jetbrains.compose.resources.stringResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CheckoutHeaderTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutHeaderCallsOnInfoClick() =
        runComposeUiTest {
            var clicked = false
            var securityInfoDescription = ""

            setContent {
                securityInfoDescription = stringResource(Res.string.content_description_security_info)
                ConektaTheme {
                    CheckoutHeader(onInfoClick = { clicked = true })
                }
            }

            onNodeWithContentDescription(securityInfoDescription).assertIsDisplayed().performClick()

            assertTrue(clicked)
        }
}
