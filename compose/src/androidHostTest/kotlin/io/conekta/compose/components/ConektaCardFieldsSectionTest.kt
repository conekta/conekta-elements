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
import io.conekta.compose.components.card.ConektaCardFieldsSection
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.placeholder_cardholder_name
import io.conekta.compose.generated.resources.placeholder_expiry
import io.conekta.compose.theme.ConektaTheme
import org.jetbrains.compose.resources.stringResource
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
            var cardholderPlaceholder = ""
            var expiryPlaceholder = ""
            setContent {
                cardholderPlaceholder = stringResource(Res.string.placeholder_cardholder_name)
                expiryPlaceholder = stringResource(Res.string.placeholder_expiry)
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

            onNodeWithText(cardholderPlaceholder).assertExists()
            onNodeWithText("0000 0000 0000 0000").assertExists()
            onNodeWithText(expiryPlaceholder).assertExists()
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
