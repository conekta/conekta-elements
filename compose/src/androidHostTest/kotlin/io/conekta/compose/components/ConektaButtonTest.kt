package io.conekta.compose.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.theme.ConektaTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ConektaButtonTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun buttonDisplaysText() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    ConektaButton(text = "Continuar", onClick = {})
                }
            }
            onNodeWithText("Continuar").assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun buttonIsEnabledByDefault() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    ConektaButton(text = "Continuar", onClick = {})
                }
            }
            onNodeWithText("Continuar").assertIsEnabled()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun buttonCanBeDisabled() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    ConektaButton(text = "Continuar", onClick = {}, enabled = false)
                }
            }
            onNodeWithText("Continuar").assertIsNotEnabled()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun buttonTriggersOnClick() =
        runComposeUiTest {
            var clicked = false
            setContent {
                ConektaTheme {
                    ConektaButton(text = "Continuar", onClick = { clicked = true })
                }
            }
            onNodeWithText("Continuar").performClick()
            assert(clicked)
        }
}
