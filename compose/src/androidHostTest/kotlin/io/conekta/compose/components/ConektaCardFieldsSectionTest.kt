package io.conekta.compose.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.theme.ConektaTheme
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ConektaCardFieldsSectionTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cardFieldsSectionRendersCoreFields() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    ConektaCardFieldsSection(
                        cardholderName = TextFieldValue(""),
                        onCardholderNameChange = {},
                        cardNumber = TextFieldValue(""),
                        onCardNumberChange = {},
                        expiryDate = TextFieldValue(""),
                        onExpiryDateChange = {},
                        cvv = TextFieldValue(""),
                        onCvvChange = {},
                    )
                }
            }

            onNode(hasText("Name as it appears on card") or hasText("Nombre como aparece en la tarjeta")).assertExists()
            onNodeWithText("0000 0000 0000 0000").assertExists()
            onNode(hasText("MM/YY") or hasText("MM/AA")).assertExists()
            onNode(hasText("CVV", substring = true) and hasSetTextAction()).assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cardFieldsSectionFormatsCardNumberInput() =
        runComposeUiTest {
            var cardNumber by mutableStateOf(TextFieldValue(""))

            setContent {
                ConektaTheme {
                    ConektaCardFieldsSection(
                        cardholderName = TextFieldValue(""),
                        onCardholderNameChange = {},
                        cardNumber = cardNumber,
                        onCardNumberChange = { cardNumber = it },
                        expiryDate = TextFieldValue(""),
                        onExpiryDateChange = {},
                        cvv = TextFieldValue(""),
                        onCvvChange = {},
                    )
                }
            }

            onNodeWithText("0000 0000 0000 0000").performTextInput("42424242")
            onNodeWithText("4242 4242").assertIsDisplayed()
        }
}
