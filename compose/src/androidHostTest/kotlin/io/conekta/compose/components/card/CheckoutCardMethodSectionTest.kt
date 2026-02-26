package io.conekta.compose.components.card

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_method_card
import io.conekta.compose.generated.resources.placeholder_cardholder_name_checkout
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.theme.ConektaTheme
import org.jetbrains.compose.resources.stringResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CheckoutCardMethodSectionTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutCardMethodItemCallsOnClick() =
        runComposeUiTest {
            var clicked = false
            var methodLabel = ""
            setContent {
                methodLabel = stringResource(Res.string.checkout_method_card)
                ConektaTheme {
                    CheckoutCardMethodItem(
                        selected = false,
                        onClick = { clicked = true },
                    )
                }
            }

            onNodeWithText(methodLabel).assertIsDisplayed().performClick()
            assertTrue(clicked)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutCardMethodSectionShowsCardFields() =
        runComposeUiTest {
            var methodLabel = ""
            var cardholderPlaceholder = ""
            setContent {
                methodLabel = stringResource(Res.string.checkout_method_card)
                cardholderPlaceholder = stringResource(Res.string.placeholder_cardholder_name_checkout)
                ConektaTheme {
                    CheckoutCardMethodSection(
                        onSelect = {},
                        cardholderName = TextFieldValue(""),
                        onCardholderNameChange = {},
                        cardNumber = TextFieldValue(""),
                        onCardNumberChange = {},
                        expiryDate = TextFieldValue(""),
                        onExpiryDateChange = {},
                        cvv = TextFieldValue(""),
                        onCvvChange = {},
                        cardholderNamePlaceholderOverride = cardholderPlaceholder,
                    )
                }
            }

            onNodeWithText(methodLabel).assertIsDisplayed()
            onNodeWithText(cardholderPlaceholder).assertIsDisplayed()
            onNodeWithText("0000 0000 0000 0000").assertIsDisplayed()
        }
}
