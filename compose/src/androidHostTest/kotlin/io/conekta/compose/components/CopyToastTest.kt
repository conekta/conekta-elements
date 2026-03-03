package io.conekta.compose.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.theme.ConektaTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CopyToastTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun copyToastDisplaysProvidedText() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CopyToast(text = "CLABE copiada")
                }
            }

            onNodeWithText("CLABE copiada").assertIsDisplayed()
        }
}
